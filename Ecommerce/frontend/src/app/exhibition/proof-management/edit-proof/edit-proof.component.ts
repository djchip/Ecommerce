import { TranslateService } from '@ngx-translate/core';
import { ProofManagementService } from './../proof-management.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import Swal from 'sweetalert2';
import { formatDate } from '@angular/common';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-edit-proof',
  templateUrl: './edit-proof.component.html',
  styleUrls: ['./edit-proof.component.scss']
})
export class EditProofComponent implements OnInit {

  @Output() afterEditProof = new EventEmitter<String>();

  public editProofForm: FormGroup;
  public editProofFormSubmitted = false;
  public data;
  public id;
  public mergedPwdShow = false;
  public nameFile;
  public onFile = false;
  public listUnit = [];
  public listDocumentType = [];
  public attachments: FileList;
  public fileList = [];
  public listField = [];
  public unitLoading = false;
  public exh_fileId: any;
  public fileUpload: File;
  public fileUploadNoContent: File;
  public basicDPdata: NgbDateStruct;
  public idProof: any;
  public numberOfFile: any;
  mySwitch: boolean = false;
  public currentLang = this._translateService.currentLang;
  public isErr = false;
  public listFileName = [];
  public listFileErr = [];
  public FileLength= 0;

  constructor(
    private formBuilder: FormBuilder,
    private service: ProofManagementService,
    public _translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.getListUnit();
    this.initForm();
    this.id = window.localStorage.getItem("id");
    this.getProofDetail();
    this.getListDocumentType();
    this.getListField();
  }

  initForm() {
    this.editProofForm = this.formBuilder.group({
      id: [''],
      proofName: ['', [Validators.required]],
      proofNameEn: [''],
      proofCode: ['', [Validators.required]],
      documentType: [null, [Validators.required]],
      numberSign: [''],
      releaseDate: [''],
      signer: [''],
      field: [null, [Validators.required]],
      releaseBy: [null, [Validators.required]],
      description: [''],
      descriptionEn: [''],
      note: [''],
      noteEn: [''],
      unit: [null],
      attachments: [''],
      exhFile: ['']
    })
  }

  onFileChange(event) {
    this.fileList = [];
    if (event.target.files.length > 0) {
      const fileD = event.target.files;
      this.attachments = event.target.files;
      this.editProofForm.patchValue({
        attachments: this.attachments
      })
      for (let i = 0; i < event.target.files.length; i++) {
        this.fileList.push(event.target.files[i].name)
      }
    }
    this.numberOfFile = event.target.files.length;
    console.log('second' + this.attachments)
  }

  // updateFile(){
  //   this.editProofFormSubmitted = true;
  //   if(this.editProofForm.invalid){
  //     return;
  //   }
  //   let params = {
  //     method: "POST",
  //     content: {proofId: this.idProof, fileNumber: this.attachments.length},
  //   };
  //   for(let i = 0; i < this.attachments.length; i++) {
  //     this.fileUpload = this.attachments[i];
  //     this.service.updateFile(params, this.fileUpload)
  //     .then( (data) => {
  //       let response = data;
  //       if (response.code === 0) {
  //       } else {
  //       }
  //     })
  //   }
  // }

  getListField() {
    let params = {
      method: "GET"
    };
    this.service
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

  getListDocumentType() {
    let params = {
      method: "GET"
    };
    this.service
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

  getListUnit() {
    let params = {
      method: "GET"
    };
    this.service
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

  fillForm() {
    
    var date = new Date(this.data.entity.releaseDate != null ? this.data.entity.releaseDate : null);
    const format = 'yyyy-MM-dd';
    const locale = 'en-US';
    const formattedDate = formatDate(date, format, locale) != null ? formatDate(date, format, locale) : null;
    if (this.data.entity.releaseDate != null) {
      this.editProofForm.patchValue({
        id: this.data.entity.id != null ? this.data.entity.id : '',
        proofName: this.data.entity.proofName != null ? this.data.entity.proofName : '',
        proofNameEn: this.data.entity.proofNameEn != null ? this.data.entity.proofNameEn : '',
        proofCode: this.data.entity.proofCode,
        documentType: this.data.entity.documentType != null ? this.data.entity.documentType : '',
        numberSign: this.data.entity.numberSign != null ? this.data.entity.numberSign : '',
        releaseDate: formattedDate,
        signer: this.data.entity.signer != null ? this.data.entity.signer : '',
        field: this.data.entity.field != null ? this.data.entity.field : '',
        releaseBy: this.data.entity.releaseBy != null ? this.data.entity.releaseBy : '',
        description: this.data.entity.description != null ? this.data.entity.description : '',
        descriptionEn: this.data.entity.descriptionEn != null ? this.data.entity.descriptionEn : '',
        note: this.data.entity.note != null ? this.data.entity.note : '',
        noteEn: this.data.entity.noteEn != null ? this.data.entity.noteEn : '',
        unit: this.data.unit,
        attachments: this.data.file != null ? this.data.file : '',
      })
    } else {
      this.editProofForm.patchValue({
        id: this.data.entity.id != null ? this.data.entity.id : '',
        proofName: this.data.entity.proofName != null ? this.data.entity.proofName : '',
        proofNameEn: this.data.entity.proofNameEn != null ? this.data.entity.proofNameEn : '',
        proofCode: this.data.entity.proofCode,
        documentType: this.data.entity.documentType != null ? this.data.entity.documentType : '',
        numberSign: this.data.entity.numberSign != null ? this.data.entity.numberSign : '',
        signer: this.data.entity.signer != null ? this.data.entity.signer : '',
        field: this.data.entity.field != null ? this.data.entity.field : '',
        releaseBy: this.data.entity.releaseBy != null ? this.data.entity.releaseBy : '',
        description: this.data.entity.description != null ? this.data.entity.description : '',
        descriptionEn: this.data.entity.descriptionEn != null ? this.data.entity.descriptionEn : '',
        noteEn: this.data.entity.noteEn != null ? this.data.entity.noteEn : '',
        note: this.data.entity.note != null ? this.data.entity.note : '',
        unit: this.data.unit,
        attachments: this.data.file != null ? this.data.file : '',
      })
    }
    this.numberOfFile = this.data.file.length;
    // console.log('số lượn file ' + this.data.file.length)
    this.FileLength=this.data.file.length;
    if(this.FileLength >1){
      this.mySwitch = true;    }
  }

  get EditProofForm() {
    return this.editProofForm.controls;
  }

  changeCheckbox(e) {
    // if(this.FileLength >1){
    //   e.target.checked = true;
    // }
    
    if (e.target.checked) {
      this.mySwitch = true;
    }
    else {
      this.mySwitch = false;
    }
    this.fileList = [];
    this.getProofDetail();
    console.log(e.target.checked+"= e.target.checked");
    
  }

  async getProofDetail() {
    if (this.id !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailProof(params, this.id)
        .then((data) => {
          this.idProof = this.id;
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

  getFileNameByProofId() {
    let paramsGet = {
      method: "GET", proofId: this.idProof,
    };
    this.service.getFileNameByProofId(paramsGet)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listFileName = response.content;
          console.log('file name ' + this.listFileName)
        }
      })
  }


  editProof() {
    this.editProofFormSubmitted = true;
    if (this.editProofForm.value.proofName !== '') {
      this.editProofForm.patchValue({
        proofName: this.editProofForm.value.proofName.trim()
      })
    }
    if (this.editProofForm.invalid) {
      return;
    }
    let content = this.editProofForm.value;
    content.exhFile = this.exh_fileId;
    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editProof(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          if (typeof (this.attachments) != 'undefined') {
            let params = {
              method: "POST",
              content: { proofId: this.idProof, fileNumber: this.attachments.length },
            };
            let paramsDelete = {
              method: 'DELETE'
            }
            this.service.deleteFile(paramsDelete, this.idProof);
            for (let i = 0; i < this.attachments.length; i++) {
              this.fileUpload = this.attachments[i];
              this.service.updateFile(params, this.fileUpload)
                .then((data) => {
                  let responseUpload = data;
                  if (responseUpload.code === 0 && i == this.attachments.length - 1) {
                    Swal.close();
                    
                    Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
                      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                    }).then((result) => {
                      this.afterEditProof.emit('completed');
                    });
                  }
                })
                .catch((error) => {
                  this.service.updateFileWithoutFileContent(params, this.attachments[i])
                  .then((data) => {
                    let responseUpload = data;
                    if (responseUpload.code === 0 && i == this.attachments.length - 1) {
                      Swal.close();
                      Swal.fire({
                        icon: "success",
                        title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
                        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                      }).then((result) => {
                        this.afterEditProof.emit('completed');
                      });
                    }
                  })
                  // Swal.close();
                  // Swal.fire({
                  //   icon: "warning",
                  //   title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.CANNOT_READ_FILE_CONTENT'),
                  //   confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                  // }).then((result) => {
                  // this.service.deleteFile(paramsDelete, this.idProof);
                  // for (let i = 0; i < this.attachments.length; i++) {
                  // this.service.updateFileWithoutFileContent(params, this.fileUpload)
                  //     .then((data) => {
                  //       let responseUpload = data;
                  //       if (responseUpload.code === 0 && i == this.attachments.length - 1) {
                  //         Swal.close();
                  //         Swal.fire({
                  //           icon: "success",
                  //           title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
                  //           confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                  //         })
                  //         .then((result) => {
                  //           this.afterEditProof.emit('completed');
                  //         });
                  //       }
                  //     })
                  // }
                  // console.log('NO CONTENT');
                  // this.service.updateFileWithoutFileContent(params, this.fileUpload)
                  //   .then((data) => {
                  //     Swal.close();
                  //     let responseUpload = data;
                  //     if (responseUpload.code === 0) {
                  //       Swal.fire({
                  //         icon: "success",
                  //         title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
                  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                  //       }).then((result) => {
                  //         this.afterEditProof.emit('completed');
                  //       });
                  //     }
                  //   })
                });
              // });
            }


            

            // if (this.isErr = true) {
            //   Swal.close();
            //   Swal.fire({
            //     icon: "warning",
            //     title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.CANNOT_READ_FILE_CONTENT'),
            //     confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            //   }).then((result) => {
            //     Swal.showLoading();
            //     let paramsDelete = {
            //       method: 'DELETE'
            //     }
            //     this.service.deleteFile(paramsDelete, this.idProof);
            //     for (let i = 0; i < this.attachments.length; i++) {
            //       this.fileUpload = this.attachments[i];
            //       this.service.updateFileWithoutFileContent(params, this.fileUpload)
            //         .then((data) => {
            //           let responseUpload = data;
            //           if (responseUpload.code === 0 && i == this.attachments.length - 1) {
            //             Swal.close();
            //             Swal.fire({
            //               icon: "success",
            //               title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
            //               confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            //             }).then((result) => {
            //               this.afterEditProof.emit('completed');
            //             });
            //           }
            //         })
            //     }
            //     // console.log('NO CONTENT');
            //     // this.service.updateFileWithoutFileContent(params, this.fileUpload)
            //     //   .then((data) => {
            //     //     Swal.close();
            //     //     let responseUpload = data;
            //     //     if (responseUpload.code === 0) {
            //     //       Swal.fire({
            //     //         icon: "success",
            //     //         title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
            //     //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            //     //       }).then((result) => {
            //     //         this.afterEditProof.emit('completed');
            //     //       });
            //     //     }
            //     //   })
            //   });
            // }
          }
          else {
            Swal.close();
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.UPDATE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              this.afterEditProof.emit('completed');
            });
          }
        } else if (response.code === 100) {
          console.log(100);
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.PROOF_NAME_EXIST'),
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

  resetForm() {
    this.fillForm();
  }


}
