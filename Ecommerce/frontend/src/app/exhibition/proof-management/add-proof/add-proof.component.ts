import { Component, OnInit, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { ProofManagementService } from '../proof-management.service';
import Swal from "sweetalert2";
import { FileUploader } from 'ng2-file-upload';
import { TranslateService } from '@ngx-translate/core';
import { TreeModel, TreeNode } from '@circlon/angular-tree-component';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DirectoryService } from '../../directory/directory.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { OrganizationService } from 'app/exhibition/organization/organization.service';

@Component({
  selector: 'app-add-proof',
  templateUrl: './add-proof.component.html',
  styleUrls: ['./add-proof.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AddProofComponent implements OnInit {

  @Output() afterCreateProof = new EventEmitter<string>();

  public uploader: FileUploader;
  public contentHeader: object;
  public addProofForm: FormGroup;
  public addProofFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public attachments: FileList;
  public listUnit = [];
  public listField = [];
  public listDocumentType = [];
  public listStandard = [];
  standardSet = null;
  public criteria;
  public listCode = "";
  public list = [];
  public unitLoading = false;
  public listExhibitionCode = [];
  public listPrograms = [];
  public fileUpload: File;
  public exh_fileId: any;
  mySwitch: boolean = false;
  useFile: boolean = false;
  public listFile = [];
  public idProof = [];
  public unitOfCreator = null;
  public currentLang = this._translateService.currentLang;
  public idProgram;
  public programId;
  public encodeBy = true;
  public hasFile = false;
  public haveFile = false;
  public haveFileSearchWeb = false;
  public selectedTreeList = []
  public selectedStaList = []
  public test = false;


  constructor(
    private directoryService: DirectoryService,
    private formBuilder: FormBuilder,
    private service: ProofManagementService,
    private orgService: OrganizationService,
    public _translateService: TranslateService,
    private modalService: NgbModal,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }
  ngOnInit(): void {
    this.uploader = new FileUploader({
      isHTML5: true
    });
    localStorage.removeItem('fileName');
    localStorage.removeItem('content');
    localStorage.removeItem('type');
    this.getListUnit();
    this.getListDocumentType();
    this.initForm();
    this.getListPrograms();
    this.getListField();
    this.getUnitByCreator();
    // console.log("FILE NAME " + window.localStorage.getItem('fileName'));
    // if (window.localStorage.getItem('fileName') != null) {
    //   this.haveFileSearchWeb = true;
    // }
  }

  ngAfterViewInit() {
    setInterval(() => {
      if (window.localStorage.getItem('fileName') != null) {
        this.hasFile = true;
      } else {
        this.hasFile = false;
      }
    }, 2000);
  }

  removeFileSearchWeb() {
    localStorage.removeItem('fileName');
    localStorage.removeItem('content');
    localStorage.removeItem('type');
  }

  initForm() {
    this.addProofForm = this.formBuilder.group(
      {
        proofName: ['', [Validators.required]],
        proofNameEn: [''],
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
        programId: [null, [Validators.required]],
        listExhFile: [null],
      },
    );
  }
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  onFileChange(event) {
    if (event.target.files.length > 0) {
      this.haveFile = true;
      const fileD = event.target.files[0];
      this.attachments = event.target.files;
    }
  }

  changeCheckbox(e) {
    if (e.target.checked) {
      this.mySwitch = true;
    }
    else {
      this.mySwitch = false;
    }
  }

  // changeCheckboxFile(e) {
  //   if (e.target.checked) {
  //     this.useFile = true;
  //   }
  //   else {
  //     this.useFile = false;
  //   }
  // }

  get AddProofForm() {
    return this.addProofForm.controls;
  }

  getUnitByCreator() {
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

  fillForm() {
    this.addProofForm.patchValue({
      releaseBy: this.unitOfCreator.id
    })
  }

  getListPrograms() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.directoryService
      .getListPrograms(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listPrograms = data.content;
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

  addFile() {
    this.addProofFormSubmitted = true;
    if (this.addProofForm.invalid) {
      return;
    }
    let params = {
      method: "POST",
      content: { proofId: this.idProof, fileNumber: this.attachments.length, proofCode: this.listCode },
    };
    for (let i = 0; i < this.attachments.length; i++) {
      this.fileUpload = this.attachments[i];
      this.service.addSingleFile(params, this.fileUpload)
        .then((data) => {
          this.exh_fileId = data;
          let response = data;
          if (response.code === 0) {
          } else {
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

  addProof() {
    this.addProofFormSubmitted = true;
    if (this.addProofForm.value.proofName !== '') {
      this.addProofForm.patchValue({
        proofName: this.addProofForm.value.proofName.trim()
      })
    }
    if (this.addProofForm.invalid) {
      return;
    }
    if (this.list.length == 0) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.STA_CRI_PROOF_CODE_REQUIRE'),
      }).then((result) => {
      });
      return;
    }

    if (!this.hasFile && this.uploader.queue.length <= 0) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.ATTACHMENTS_REQUIRE'),
      }).then((result) => {
      });
      return;
    }
    let content = this.addProofForm.value;
    content.standardIds = this.selectedStaList;
    content.criteriaIds = this.selectedTreeList;
    content.proofCode = this.listCode;
    content.programId = this.idProgram;
    content.exhFile = this.exh_fileId;
    content.listExhFile = this.listFile;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addProof(params)
      .then((data) => {

        let response = data;
        if (response.code === 0) {

          for (let i = 0; i < data.content.length; i++) {
            this.idProof.push(data.content[i].id)
          }

          let filePathSearchWeb = window.localStorage.getItem('fileName');
          let fileContentSearchWeb = window.localStorage.getItem('content');
          let typeOfFileSearchWeb = window.localStorage.getItem('type');

          let params;
          let paramsSearchWeb;
          if (this.uploader.queue.length > 0) {
            params = {
              method: "POST",
              content: {
                proofId: this.idProof,
                fileNumber: this.uploader.queue.length,
                proofCode: this.listCode
              },
            };

            paramsSearchWeb = {
              method: "POST",
              content: {
                proofId: this.idProof,
                fileNumber: this.uploader.queue.length,
                proofCode: this.listCode,
                filePathSearchWeb: filePathSearchWeb,
                fileContentSearchWeb: fileContentSearchWeb,
                typeOfFileSearchWeb: typeOfFileSearchWeb,
              },
            };
          } else {
            params = {
              method: "POST",
              content: {
                proofId: this.idProof,
                fileNumber: 0,
                proofCode: this.listCode
              },
            };

            paramsSearchWeb = {
              method: "POST",
              content: {
                proofId: this.idProof,
                fileNumber: 0,
                proofCode: this.listCode,
                filePathSearchWeb: filePathSearchWeb,
                fileContentSearchWeb: fileContentSearchWeb,
                typeOfFileSearchWeb: typeOfFileSearchWeb,
              },
            };
          }

          if (this.uploader.queue.length > 0) {
            for (let i = 0; i < this.uploader.queue.length; i++) {
              this.fileUpload = this.uploader.queue[i]._file;
              this.service.addSingleFile(params, this.fileUpload)
                .then((data) => {
                  this.exh_fileId = data;
                  let responseUploadFile = data;
                  if (responseUploadFile.code === 0 && i == this.uploader.queue.length - 1 && !this.hasFile) {
                    Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.ADD_SUCCESS'),
                      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                    }).then((result) => {
                      this.afterCreateProof.emit('completed');
                    });
                  }
                })
                .catch((error) => {
                  this.service.addSingleFileWithoutFileContent(params, this.fileUpload)
                    .then((data) => {
                      this.exh_fileId = data;
                      let responseUploadFile = data;
                      if (responseUploadFile.code === 0 && i == this.uploader.queue.length - 1) {
                        Swal.fire({
                          icon: "success",
                          title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.ADD_SUCCESS'),
                          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                        }).then((result) => {
                          this.afterCreateProof.emit('completed');
                        });
                      }
                    })
                });
            }
          }

          if (filePathSearchWeb != null) {
            this.service.uploadFileSearchWeb(paramsSearchWeb).then((data) => {
              let response = data;
              if (response.code === 0) {
                Swal.fire({
                  icon: "success",
                  title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.ADD_SUCCESS'),
                  confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                }).then((result) => {
                  localStorage.removeItem('fileName');
                  localStorage.removeItem('content');
                  localStorage.removeItem('type');
                  this.afterCreateProof.emit('completed');
                });
              }
            })
          }
        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.PROOF_CODE_EXIST'),
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
        console.log("catch");
        Swal.close();
        Swal.fire({
          icon: "error",
          title: "Không kết nối được tới hệ thống hihi.",
          confirmButtonText: "OK",
        });
      });
  }

  getListStaAndListCriSelected() {
    let standard = window.localStorage.getItem("standard");
    let criteria = window.localStorage.getItem("criteria");
    let params = {
      method: "POST",
      content: { standard: standard, criteria: criteria }
    };
    this.service.getListStaAndListCriSelected(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          console.log("SUCCESS")
        } else {
        }
      })
  }

  getTreeByProgram() {
    if (this.addProofForm.value.programId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTree(params, this.addProofForm.value.programId.id)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.nodesFilter = response.content;
          } else {
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

  getTreeByProgramEn() {
    if (this.addProofForm.value.programId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTreeEn(params, this.addProofForm.value.programId.id)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.nodesFilter = response.content;
          } else {
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


  getTreeStaByProgram() {
    if (this.addProofForm.value.programId.id) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getTreeSta(params, this.addProofForm.value.programId.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.nodesFilterSta = response.content;
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

  getTreeStaByProgramEn() {
    if (this.addProofForm.value.programId.id) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getTreeStaEn(params, this.addProofForm.value.programId.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.nodesFilterSta = response.content;
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

  getOrgByProgramId() {
    if (this.programId !== '') {
      let params = {
        method: "GET"
      };
      console.log('fe ' + this.programId)
      this.orgService
        .findOrgByProgramId(params, this.programId)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.encodeBy = this.data.encode;

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

  onChange() {
    this.programId = this.addProofForm.value.programId.id;
    if (this.programId !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      this.orgService
        .findOrgByProgramId(params, this.programId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.encodeBy = this.data.encode;
          } else {
            this.encodeBy = false;
          }
        })
    }
    console.log(this.currentLang + " LAN");
    if (this.currentLang === 'vn') {
      this.getTreeStaByProgram();
      this.getTreeByProgram();
    } else {
      this.getTreeStaByProgramEn();
      this.getTreeByProgramEn();
    }

    this.list = [];
    this.listCode = "";
    window.localStorage.setItem("idPro", this.addProofForm.value.programId.id);

    if (window.localStorage.getItem('fileName') != null) {
      this.haveFileSearchWeb = true;
    }
  }

  reloadListCode(newList) {
    let check = 0;
    this.listCode = ""
    newList.forEach(element => {
      console.log('sta ' + element.standardId)
      console.log('cri ' + element.criteriaId)
      this.listCode = this.listCode + element.standardId + "," + element.criteriaId + "," + element.exhibitionCode + ";"
      if (this.listCode.substring(this.listCode.length - 2) == '.;') {
        check = 1;
      }
    });
    this.list = newList;
    if (this.listCode != '') {
      if (check != 1) {
        Swal.fire({
          icon: "success",
          title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.INPUT_CODE_SUCCESS'),
        }).then((result) => {
          document.getElementById("closebutton").click();
        });
      } else {
        this.listCode = "";
        this.list = [];
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.PROOF_CODE_REQUIRE'),
        }).then((result) => {
          document.getElementById("closebutton").click();
        });
      }
    } else {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.INPUT_REQUIRED'),
      }).then((result) => {
        document.getElementById("closebutton").click();
      });
    }
  }

  openModalInputProofCode(modalSM) {
    window.localStorage.removeItem("standard");
    window.localStorage.setItem("standard", this.selectedStaList + '');
    window.localStorage.removeItem("criteria");
    window.localStorage.setItem("criteria", this.selectedTreeList + '');
    this.idProgram = window.localStorage.getItem("idPro");
    this.getListStaAndListCriSelected();
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });

  }

  public nodesFilter = [];
  public nodesFilterSta = [];


  // filter
  public optionsFilter = {
    useCheckbox: true
  };



  /**
  * filterFn
  *
  * @param value
  * @param treeModel
  */
  filterFn(value: string, treeModel: TreeModel) {
    treeModel.filterNodes((node: TreeNode) => fuzzysearch(value, node.data.name));
  }

  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);
  }

  onSelectSta(event) {
    this.selectedStaList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);

    console.log('Selected ' + this.selectedStaList);
  }

  openModalAddProof(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
}
// fuzzysearch function
function fuzzysearch(needle: string, haystack: string) {
  const haystackLC = haystack.toLowerCase();
  const needleLC = needle.toLowerCase();

  const hlen = haystack.length;
  const nlen = needleLC.length;

  if (nlen > hlen) {
    return false;
  }
  if (nlen === hlen) {
    return needleLC === haystackLC;
  }
  outer: for (let i = 0, j = 0; i < nlen; i++) {
    const nch = needleLC.charCodeAt(i);

    while (j < hlen) {
      if (haystackLC.charCodeAt(j++) === nch) {
        continue outer;
      }
    }
    return false;
  }
  return true;
}