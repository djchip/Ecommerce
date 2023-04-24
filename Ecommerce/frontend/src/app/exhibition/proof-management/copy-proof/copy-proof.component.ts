import { Component, OnInit, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { ProofManagementService } from '../proof-management.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { TreeModel, TreeNode } from '@circlon/angular-tree-component';
import { NgbDateStruct, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DirectoryService } from '../../directory/directory.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { OrganizationService } from 'app/exhibition/organization/organization.service';

@Component({
  selector: 'app-copy-proof',
  templateUrl: './copy-proof.component.html',
  styleUrls: ['./copy-proof.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CopyProofComponent implements OnInit {

  @Output() afterCreateProof = new EventEmitter<string>();

  public contentHeader: object;
  public copyProofForm: FormGroup;
  public copyProofFormSubmitted = false;
  public mergedPwdShow = false;
  public data;
  public listUnit = [];
  public listField = [];
  public listDocumentType = [];
  public listStandard = [];
  standardSet = null;
  public id;
  public criteria;
  public listCode = "";
  public list = [];
  public unitLoading = false;
  public listExhibitionCode = [];
  public listPrograms = [];
  public fileUpload: File;
  public listFile = [];
  public idProof = [];
  public unitOfCreator = null;
  public currentLang = this._translateService.currentLang;
  public idProgram;
  public programId;
  public encodeBy = true;

  constructor(
    private directoryService: DirectoryService,
    private formBuilder: FormBuilder,
    private orgService: OrganizationService,
    private service: ProofManagementService,
    public _translateService: TranslateService,
    private modalService: NgbModal,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    this.initForm();
    this.getListPrograms();
    this.id = window.localStorage.getItem("id");
  }

  initForm() {
    this.copyProofForm = this.formBuilder.group(
      {
        id: [''],
        programId: [null, [Validators.required]],
      },
    );
  }

  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get CopyProofForm() {
    return this.copyProofForm.controls;
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
    if (this.copyProofForm.value.programId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTree(params, this.copyProofForm.value.programId.id)
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
    if (this.copyProofForm.value.programId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTreeEn(params, this.copyProofForm.value.programId.id)
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

  copyProof() {
    this.copyProofFormSubmitted = true;
    if (this.copyProofForm.invalid) {
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
    let content = this.copyProofForm.value;
    let proId = this.copyProofForm.value.programId.id;
    content.proofId = this.id;
    content.proofCode = this.listCode;
    content.programId = this.idProgram;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .copyProof(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
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
  }

  getTreeStaByProgram() {
    if (this.copyProofForm.value.programId.id) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getTreeSta(params, this.copyProofForm.value.programId.id)
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
    if (this.copyProofForm.value.programId.id) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getTreeStaEn(params, this.copyProofForm.value.programId.id)
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
    this.programId = this.copyProofForm.value.programId.id;
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
    if (this.currentLang === 'vn') {
      this.getTreeStaByProgram();
      this.getTreeByProgram();
    } else {
      this.getTreeStaByProgramEn();
      this.getTreeByProgramEn();
    }

    this.list = [];
    window.localStorage.setItem("idPro", this.copyProofForm.value.programId.id);
  }

  reloadListCode(newList) {
    let check = 0;
    this.listCode = "";
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


