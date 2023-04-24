import { TranslateService } from '@ngx-translate/core';
import { DocumentService } from './../document.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import Swal from 'sweetalert2';
import { ProofManagementService } from 'app/exhibition/proof-management/proof-management.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-add-document',
  templateUrl: './add-document.component.html',
  styleUrls: ['./add-document.component.scss']
})
export class AddDocumentComponent implements OnInit {

  @Output() afterCreateDocument = new EventEmitter<string>();

  public addDocumentForm: FormGroup;
  public addDocumentFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public fileUpload: File;
  public attachments: FileList;
  public onFile = false;
  public listDocumentType= [];
  public listUnit = [];
  public listField = [];
  public documentId;
  public unitOfCreator = null;
  public currentLang = this._translateService.currentLang;

  constructor(
    private formBuilder: FormBuilder, 
    private service: DocumentService, 
    private serviceProof: ProofManagementService, 
    public _translateService: TranslateService,
    private modalService: NgbModal,
    private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      });
    }

  ngOnInit(): void {
    this.initForm();
    this.getListDocumentType();
    this.getListField();
    this.getListUnit();
    this.getUnitByCreator();
  }

  get AddDocumentForm(){
    return this.addDocumentForm.controls;
  }

  initForm(){
    this.addDocumentForm = this.formBuilder.group({
      documentNumber: [''],
      documentName: ['', [Validators.required]],
      documentNameEn: [''],
      documentType: [null, [Validators.required]],
      releaseDate: [''],
      signer: [''],
      field: [null],
      releaseBy: [null],
      description: [''],
      descriptionEn: [''],
      unit: [null],
      attachments: ['',[Validators.required]],
    })
  }

  onFileChange(event){
    if(event.target.files.length > 0){
      const fileD = event.target.files[0];
      this.attachments = event.target.files;
    }
  }

  getListDocumentType(){
    let params = {
      method: "GET"
    };
    this.serviceProof
      .getListDocumentType(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listDocumentType = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listDocumentType = [];
          }
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }

  fillForm(){
    this.addDocumentForm.patchValue({
      releaseBy : this.unitOfCreator.id,
    })
    console.log('patth' + this.unitOfCreator.id)
  }

  getUnitByCreator(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getUnitByCreater(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.unitOfCreator = data.content;
          this.fillForm();
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
    })
  }

  getListField(){
    let params = {
      method: "GET"
    };
    this.serviceProof
      .getListField(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listField = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listField = [];
          }
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }

  getListUnit(){
    let params = {
      method: "GET"
    };
    this.serviceProof
      .getListUnit(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUnit = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listUnit = [];
          }
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }


  addDocument(){
    this.addDocumentFormSubmitted = true;

    if (this.addDocumentForm.value.documentName !== '') {
      this.addDocumentForm.patchValue({
        documentName: this.addDocumentForm.value.documentName.trim()
      })
    }

    if(this.addDocumentForm.invalid){
      return;
    }
    let content = this.addDocumentForm.value;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
        .addDocument(params)
        .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.documentId = data.content.id;
          console.log('doc id' + this.documentId)
          let params = {
            method: "POST",
            content: {documentId: this.documentId}
          };
          for(let i = 0; i < this.attachments.length; i++){
            this.fileUpload = this.attachments[i];
            this.service.addSingleFile(params, this.fileUpload)
            .then((data) => {
              let responseUploadFile = data;
              if(responseUploadFile.code === 0 && i == this.attachments.length - 1){
                Swal.close();
                Swal.fire({
                  icon: "success",
                  title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.ADD_SUCCESS'),
                  confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                }).then((result) => {
                  this.afterCreateDocument.emit('completed');
                });
              }
            })
            .catch((error) => {
              
              Swal.fire({
                // icon: "warning",
                // title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.CANNOT_READ_FILE_CONTENT'),
                // confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
              }).then((result) => {
                this.service.updateFileWithoutFileContent(params, this.fileUpload)
                .then( (data) => {
                  Swal.close();
                  let responseUploadFile = data;
                  if(responseUploadFile.code === 0){
                    Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.ADD_SUCCESS'),
                      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                    }).then((result) => {
                      this.afterCreateDocument.emit('completed');
                    });
                  }
                })
              });
            });
          //   .catch((error) => {
          //     console.log("catch");
          //     Swal.close();
          //     Swal.fire({
          //       icon: "error",
          //       title: "Không upload được file.",
          //       confirmButtonText: "OK",
          //     });
          //   });
          }
        } else if (response.code === 3) {
          console.log(3);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.DOCUMENTNUMBER_EXIST'),
          }).then((result) => {
          });
        }else if(response.code ===100){
          console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.DOCUMENTNAME_EXIST'),
          }).then((result) => {
          });
        } else {
          console.log("err");
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        console.log("catch");
        Swal.close();
        Swal.fire({
          icon: "error",
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });
  }

  
}
