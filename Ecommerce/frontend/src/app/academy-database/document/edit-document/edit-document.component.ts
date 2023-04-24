import { locale } from './../../../menu/i18n/en';
import { TranslateService } from '@ngx-translate/core';
import { DocumentService } from './../document.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import Swal from 'sweetalert2';
import { formatDate } from '@angular/common';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { ProofManagementService } from 'app/exhibition/proof-management/proof-management.service';

@Component({
  selector: 'app-edit-document',
  templateUrl: './edit-document.component.html',
  styleUrls: ['./edit-document.component.scss']
})
export class EditDocumentComponent implements OnInit {

  @Output() afterEditDocument = new EventEmitter<String>();

  public editDocumentForm: FormGroup;
  public editDocumentFormSubmitted = false;
  public data;
  public id;
  public mergedPwdShow = false;
  public nameFile;
  public attachments: FileList;
  public fileUpload: File;
  public onFile = false;
  public listDocumentType = [];
  public listUnit = [];
  public listField = [];
  public currentLang = this._translateService.currentLang;
  public fileList = [];
  public docId: any;

  constructor(
    private formBuilder: FormBuilder,
    private serviceProof: ProofManagementService,
    private service: DocumentService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    this.initForm();
    this.id = window.localStorage.getItem("id");
    this.getDocumentDetail();
    this.getListDocumentType();
    this.getListField();
    this.getListUnit();
  }

  initForm() {
    this.editDocumentForm = this.formBuilder.group({
      id: [''],
      documentNumber: [''],
      documentName: ['', [Validators.required]],
      documentNameEn: [''],
      documentType: [null, [Validators.required]],
      releaseDate: [''],
      signer: [''],
      field: [null],
      releaseBy: [null],
      unit: [null],
      description: [''],
      descriptionEn: [''],
      attachments: ['', [Validators.required]],
    })
  }

  onFileChange(event) {
    this.fileList = [];
    if (event.target.files.length > 0) {
      const fileD = event.target.files;
      this.attachments = event.target.files;
      this.editDocumentForm.patchValue({
        attachments: this.attachments
      })
      for (let i = 0; i < event.target.files.length; i++) {
        this.fileList.push(event.target.files[i].name)
      }
    }
  }

  getListDocumentType() {
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
          if (response.code === 2) {
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
  getListField() {
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
          if (response.code === 2) {
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
  getListUnit() {
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
          if (response.code === 2) {
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
  onDownloadFile() {
    this.service.download(this.id, this.data.file);
  }

  editDocument() {
    this.editDocumentFormSubmitted = true;

    if (this.editDocumentForm.value.documentName !== '') {
      this.editDocumentForm.patchValue({
        documentName: this.editDocumentForm.value.documentName.trim()
      })
    }
    if (this.editDocumentForm.invalid) {
      return;
    }
    let content = this.editDocumentForm.value;

    let params1 = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editDocument(params1)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          // Swal.fire({
          //   icon: "success",
          //   title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.UPDATE_SUCCESS'),
          //   confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          // }).then((result) => {
          //   this.afterEditDocument.emit('completed');
          // });
          if (typeof (this.attachments) != 'undefined') {
            let params = {
              method: "POST",
              content: { documentId: this.docId },
            };
            for (let i = 0; i < this.attachments.length; i++) {
              this.fileUpload = this.attachments[i];
              this.service.updateFile(params, this.fileUpload)
                .then((data) => {
                  let responseUpload = data;
                  if (responseUpload.code === 0 && i == this.attachments.length - 1) {
                    Swal.close();
                    Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.UPDATE_SUCCESS'),
                      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                    }).then((result) => {
                      this.afterEditDocument.emit('completed');
                    });
                  }
                })
                .catch((error) => {
                  
                  Swal.fire({
                    
                  }).then((result) => {
                    this.service.updateFileWithoutFileContent(params, this.fileUpload)
                      .then((data) => {
                        let responseUpload = data;
                        if (responseUpload.code === 0 && i == this.attachments.length - 1) {
                          Swal.close();
                          Swal.fire({
                            icon: "success",
                            title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.UPDATE_SUCCESS'),
                            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                          }).then((result) => {
                            this.afterEditDocument.emit('completed');
                          });
                        }
                      })
                  });
                });
            }
          } else {
            Swal.close();
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.UPDATE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              this.afterEditDocument.emit('completed');
            });
          }
        } else if (response.code === 100) {
          console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.DOCUMENTNAME_EXIST'),
          }).then((result) => {
          });
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
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

  fillForm() {
    var date = new Date(this.data.entity.releaseDate != null ? this.data.entity.releaseDate : null);
    const format = 'yyyy-MM-dd';
    const locale = 'en-US';
    const formattedDate = formatDate(date, format, locale) != null ? formatDate(date, format, locale) : null;
    if (this.data.entity.releaseDate != null) {
      this.editDocumentForm.patchValue({
        id: this.data.entity.id != null ? this.data.entity.id : '',
        documentNumber: this.data.entity.documentNumber != null ? this.data.entity.documentNumber : '',
        documentName: this.data.entity.documentName != null ? this.data.entity.documentName : '',
        documentNameEn: this.data.entity.documentNameEn != null ? this.data.entity.documentNameEn : '',
        documentType: this.data.entity.documentType != null ? this.data.entity.documentType : '',
        releaseDate: formattedDate,
        signer: this.data.entity.signer != null ? this.data.entity.signer : '',
        field: this.data.entity.field != null ? this.data.entity.field : null,
        releaseBy: this.data.entity.releaseBy != null ? this.data.entity.releaseBy : '',
        unit: this.data.unit,
        description: this.data.entity.description != null ? this.data.entity.description : '',
        descriptionEn: this.data.entity.descriptionEn != null ? this.data.entity.descriptionEn : '',
        attachments: this.data.file != null ? this.data.file : '',
      })
    } else {
      this.editDocumentForm.patchValue({
        id: this.data.entity.id != null ? this.data.entity.id : '',
        documentNumber: this.data.entity.documentNumber != null ? this.data.entity.documentNumber : '',
        documentName: this.data.entity.documentName != null ? this.data.entity.documentName : '',
        documentNameEn: this.data.entity.documentNameEn != null ? this.data.entity.documentNameEn : '',
        documentType: this.data.entity.documentType != null ? this.data.entity.documentType : '',
        signer: this.data.entity.signer != null ? this.data.entity.signer : '',
        field: this.data.entity.field != null ? this.data.entity.field : null,
        releaseBy: this.data.entity.releaseBy != null ? this.data.entity.releaseBy : '',
        unit: this.data.unit,
        description: this.data.entity.description != null ? this.data.entity.description : '',
        descriptionEn: this.data.entity.descriptionEn != null ? this.data.entity.descriptionEn : '',
        attachments: this.data.file != null ? this.data.file : '',
      })
    }

    this.nameFile = this.data.file;
  }

  get EditDocumentForm() {
    return this.editDocumentForm.controls;
  }

  async getDocumentDetail() {
    if (this.id !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailDocument(params, this.id)
        .then((data) => {
          this.docId = this.id;
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.data.file.forEach(element => {
              this.fileList.push(element.fileName)
            });
            this.fillForm();
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
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
  }

  resetForm() {
    this.fillForm();
  }
}
