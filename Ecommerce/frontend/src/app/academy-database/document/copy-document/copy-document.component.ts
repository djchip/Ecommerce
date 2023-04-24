import { Component, OnInit, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { ProofManagementService } from '../../../exhibition/proof-management/proof-management.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { TreeModel, TreeNode } from '@circlon/angular-tree-component';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DirectoryService } from '../../../exhibition/directory/directory.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { DocumentService } from '../document.service';
import { formatDate } from '@angular/common';
import { OrganizationService } from 'app/exhibition/organization/organization.service';

@Component({
  selector: 'app-copy-document',
  templateUrl: './copy-document.component.html',
  styleUrls: ['./copy-document.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CopyDocumentComponent implements OnInit {

  @Output() afterCreateProof = new EventEmitter<string>();

  public contentHeader: object;
  public copyDocumentForm: FormGroup;
  public copyDocumentFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
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
  public exh_fileId: any;
  mySwitch: boolean = false;
  public listFile = [];
  public idProof = [];
  public unitOfCreator = null;
  public currentLang = this._translateService.currentLang;
  public id;
  public idProgram;
  public programId;
  public encodeBy = true;


  constructor(
    private directoryService: DirectoryService,
    private formBuilder: FormBuilder,
    private service: ProofManagementService,
    private orgService: OrganizationService,
    private documentService: DocumentService,
    public _translateService: TranslateService,
    private modalService: NgbModal,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    this.id = window.localStorage.getItem("id");
    this.getDocumentDetail();
    this.getListUnit();
    this.getListDocumentType();
    this.initForm();
    this.getListPrograms();
    this.getListField();
    this.getUnitByCreator();
  }

  initForm() {
    this.copyDocumentForm = this.formBuilder.group(
      {
        id: [''],
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

  get CopyDocumentForm() {
    return this.copyDocumentForm.controls;
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

  async getDocumentDetail() {
    if (this.id !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.documentService
        .detailDocument(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.fillDocumentType();
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

  fillForm() {
    this.copyDocumentForm.patchValue({
      releaseBy: this.unitOfCreator.id
    })
  }

  fillDocumentType() {
    var date = new Date(this.data.entity.releaseDate != null ? this.data.entity.releaseDate : null);
    const format = 'yyyy-MM-dd';
    const locale = 'en-US';
    const formattedDate = formatDate(date, format, locale) != null ? formatDate(date, format, locale) : null;
    if(this.data.entity.releaseDate != null){
      this.copyDocumentForm.patchValue({
        documentType: this.data.entity.documentType != null ? this.data.entity.documentType : null,
        field: this.data.entity.field != null ? this.data.entity.field : null,
        numberSign: this.data.entity.documentNumber != null ? this.data.entity.documentNumber : '',
        signer: this.data.entity.signer != null ? this.data.entity.signer : '',
        releaseDate: formattedDate,
        description: this.data.entity.description != null ? this.data.entity.description : '',
        descriptionEn: this.data.entity.descriptionEn != null ? this.data.entity.descriptionEn : '',
        unit: this.data.unit != null ? this.data.unit : null,
      })
    }else{
      this.copyDocumentForm.patchValue({
        documentType: this.data.entity.documentType != null ? this.data.entity.documentType : null,
        field: this.data.entity.field != null ? this.data.entity.field : null,
        numberSign: this.data.entity.documentNumber != null ? this.data.entity.documentNumber : '',
        signer: this.data.entity.signer != null ? this.data.entity.signer : '',
        descriptionEn: this.data.entity.descriptionEn != null ? this.data.entity.descriptionEn : '',
        unit: this.data.unit != null ? this.data.unit : null,
      })
    }
    
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

  copyProof() {
    this.copyDocumentFormSubmitted = true;
    
    if (this.copyDocumentForm.value.proofName !== '') {
      this.copyDocumentForm.patchValue({
        proofName: this.copyDocumentForm.value.proofName.trim()
      })
    }
    if (this.copyDocumentForm.invalid) {
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
    let content = this.copyDocumentForm.value;
    let proId = this.copyDocumentForm.value.programId.id;
    content.standardIds = this.selectedStaList;
    content.criteriaIds = this.selectedTreeList;
    content.proofCode = this.listCode;
    content.programId = this.idProgram;
    content.exhFile = this.exh_fileId;
    content.listExhFile = this.listFile;
    content.id = this.id;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .copyDocumentToProof(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          Swal.close();
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.COPY_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterCreateProof.emit('completed');
          });
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
          title: "Không kết nối được tới hệ thống.",
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
    if (this.copyDocumentForm.value.programId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTree(params, this.copyDocumentForm.value.programId.id)
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
    if (this.copyDocumentForm.value.programId.id) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getTreeSta(params, this.copyDocumentForm.value.programId.id)
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

  onChange() {
    this.programId = this.copyDocumentForm.value.programId.id;
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
    this.getTreeStaByProgram();
    this.getTreeByProgram();
    this.list = [];
    window.localStorage.setItem("idPro", this.copyDocumentForm.value.programId.id);
  }

  reloadListCode(newList) {
    let check = 0;
    this.listCode = ""
    newList.forEach(element => {
      this.listCode += element.standardId + "," + element.criteriaId + "," + element.exhibitionCode + ";"
      if (this.listCode.substring(this.listCode.length - 2) == '.;') {
        check = 1;
      }
    });
    this.list = newList;
    console.log('check' + check)
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
    this.idProgram =  window.localStorage.getItem("idPro");
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
  // filterFnSta(value: string, treeModel: TreeModel) {
  //   treeModel.filterNodesSta((node: TreeNode) => fuzzysearch(value, node.data.name));
  // }
  public selectedTreeList = []
  public selectedStaList = []

  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);

    console.log('Selected ' + this.selectedTreeList);
  }

  onSelectSta(event) {
    this.selectedStaList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);

    console.log('Selected ' + this.selectedStaList);
  }

  openModalCopyProof(modalSM) {
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
