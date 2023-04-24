import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { TreeViewComponent, NodeSelectEventArgs } from '@syncfusion/ej2-angular-navigations';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ITreeOptions, TreeComponent, TreeModel, TreeNode } from '@circlon/angular-tree-component';
import { CoreTranslationService } from '@core/services/translation.service';
import { TranslateService } from '@ngx-translate/core';
import { RolesGroupManagementService } from 'app/administrators/roles-group-management/roles-group-management.service';
import Swal from 'sweetalert2';
import * as snippet from 'app/main/extensions/tree-view/tree-view.snippetcode';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { DirectoryService } from 'app/exhibition/directory/directory.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { OrganizationService } from 'app/exhibition/organization/organization.service';
import { PrivilegesProStaCriService } from '../privileges-pro-sta-cri.service';
import { ProofManagementService } from 'app/exhibition/proof-management/proof-management.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { log } from 'console';

@Component({
  selector: 'app-privileges-pro-sta-cri',
  templateUrl: './privileges-pro-sta-cri.component.html',
  styleUrls: ['./privileges-pro-sta-cri.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class PrivilegesProStaCriComponent implements OnInit {
  public tree: TreeViewComponent;
  public listRole = [];
  public getListCriDTOByUserId = [];
  public listCriteriaSelected = [];
  public selectedTreeList = [];
  public roleLoading = false;
  public contentHeader: object;
  public _snippetCodeFilter = snippet.snippetCodeFilter;
  public userId;
  public submitSave = false;
  public isInvalidRole = true;
  public isInvalidPrivileges = true;
  public isInvalidMenu = true;
  public listMenuByRole = [];
  public listOrg = [];
  public listCategory = [];
  public listUser = [];
  public usersId = null;
  public cateId = null;
  public disable = true;
  public disableUserId = true;
  public privilegesForm: FormGroup;
  public privilegesFormSubmitted = false;
  public currentLang = this._translateService.currentLang;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  // nodesFilter = [
  //   {
  //     name: 'North America',
  //     children: [
  //       {
  //         name: 'United States',
  //         children: [
  //           { name: 'New York' },
  //           { name: 'California' },
  //           { name: 'Florida' }
  //         ]
  //       },
  //       { name: 'Canada' }
  //     ]
  //   },
  //   {
  //     name: 'South America',
  //     children: [{ name: 'Argentina', children: [] }, { name: 'Brazil' }]
  //   },
  //   {
  //     name: 'Europe',
  //     children: [
  //       { name: 'England' },
  //       { name: 'Germany' },
  //       { name: 'France' },
  //       { name: 'Italy' },
  //       { name: 'Spain' }
  //     ]
  //   }
  // ];
  constructor(
    public _translateService: TranslateService,
    public service: PrivilegesProStaCriService,
    public roleService: RolesGroupManagementService,
    private _coreTranslationService: CoreTranslationService,
    private standardService: DirectoryService,
    private _changeLanguageService: ChangeLanguageService,
    private formBuilder: FormBuilder,
    private orgService: OrganizationService,
    private proofService: ProofManagementService,
    private modalService: NgbModal,
    private router: Router
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if (this.router.url === element.url) {
        this.acceptAction = element.action;
      }
    });
    this._coreTranslationService.translate(eng, vie);
    // content header
    this.contentHeader = {
      headerTitle: 'MENU.AUTH_MANAGEMENT',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/dashboard'
          },
          {
            name: 'CONTENT_HEADER.EXHIBITION',
            isLink: false,
            link: '/'
          },
          {
            name: 'MENU.PRIVILEGES_PRO_STA_CRI',
            isLink: false
          }
        ]
      }
    };
    // this.getTreeByOrgId();
    this.getListOrg();
    // this.getListCategory();
    this.getListUser();
    this.initForm();
  }

  afterCreateProof() {
    this.modalService.dismissAll();
  }

  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get PrivilegesForm() {
    return this.privilegesForm.controls;
  }

  initForm() {
    this.privilegesForm = this.formBuilder.group(
      {
        organizationId: [null, [Validators.required]],
        categoryId: [null, [Validators.required]],
        userInfos: [null, [Validators.required]]
      }
    );
  }

  isObj = false;
  isObjElse = false;
  onChange(event, model) {
    if (event == undefined) {
      console.log("TADA " + this.selectedTreeList)
      this.listCategory = [];
      this.usersId = null;
      this.cateId = null;
      this.disable = true;
      this.disableUserId = true;
      this.selectedTreeList.forEach(element => {
        console.log("ELEMENT.ID " + element.id)
        if (element.id === undefined) {
          console.log("UNDEFINE")
          this.isObj = true;
          model.getNodeById(element).setIsSelected(false);
        }
      });
      console.log("isObj " + this.isObj);
      if (this.isObj === false) {
        this.selectedTreeList.forEach(element => {
          console.log("isObj = FALSE");
          model.getNodeById(element.id).setIsSelected(false);
        });
      }
    } else {
      console.log("NHAY VAO DAY");
      
      this.usersId = null;
      this.cateId = null;
      this.disable = false;
      this.disableUserId = true;
      this.getListCategory();
      if (this.currentLang == 'vn') {
        this.getTreeByOrgId();
      } else {
        this.getTreeByOrgIdEn();
      }
      this.selectedTreeList.forEach(element => {
        if (element.id == undefined) {
          this.isObjElse = true;
          model.getNodeById(element).setIsSelected(false);
        }
      });
      if (this.isObjElse == false) {
        this.selectedTreeList.forEach(element => {
          model.getNodeById(element.id).setIsSelected(false);
        });
      }
    }
  }
  
  isObj2 = false;
  isObj2Else = false;
  onChangeCategory(event, model) {
    if (event == undefined) {
      this.usersId = null;
      this.disableUserId = true;
      this.selectedTreeList.forEach(element => {
        if (element.id == undefined) {
          this.isObj2 = true;
          model.getNodeById(element).setIsSelected(false);
        }
      });
      if (this.isObj2 == false) {
        this.selectedTreeList.forEach(element => {
          model.getNodeById(element.id).setIsSelected(false);
        });
      }
    } else {
      this.usersId = null;
      this.disableUserId = false;
      this.selectedTreeList.forEach(element => {
        if (element.id == undefined) {
          this.isObj2Else = true;
          model.getNodeById(element).setIsSelected(false);
        }
      });
      if (this.isObj2Else == false) {
        this.selectedTreeList.forEach(element => {
          model.getNodeById(element.id).setIsSelected(false);
        });
      }
    }
    
  }

  getListOrg() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.standardService
      .getListOrganization(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrg = data.content;
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

  getListCategory() {
    if (this.privilegesForm.value.organizationId.id) {
      let params = {
        method: "GET"
      };
      this.orgService
        .getListCategoriesByOrgId(params, this.privilegesForm.value.organizationId.id)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.listCategory = response.content;
          } else {
            Swal.fire({
              icon: "error",
              title: response.errorMessages,
            });
            if (response.code === 2) {
              this.listCategory = [];
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
  }

  getListUser() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.standardService
      .getListUserWithoutAdmin(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listUser = data.content;
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

  getTreeByOrgId() {
    if (this.privilegesForm.value.organizationId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTree(params, this.privilegesForm.value.organizationId.id)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.nodesFilter = response.content;
            console.log(this.nodesFilter + "NODE FILTER")
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

  getTreeByOrgIdEn() {
    if (this.privilegesForm.value.organizationId.id) {
      let params = {
        method: "GET"
      };
      this.service
        .getTreeEn(params, this.privilegesForm.value.organizationId.id)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            this.nodesFilter = response.content;
            console.log(this.nodesFilter + "NODE FILTER")
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

  public selectedProgramId = "";
  public selectedStandardId = "";
  public selectedCriteriaId = "";

  doSave() {
    this.privilegesFormSubmitted = true;
    if (this.privilegesForm.invalid) {
      return;
    }

    let params = {
      method: "POST",
      content: {
        orgId: this.privilegesForm.value.organizationId.id,
        categoryId: this.privilegesForm.value.categoryId.id,
        userId: this.privilegesForm.value.userInfos.id,
        listCriteriaId: this.selectedTreeList
      },
    };

    Swal.showLoading();
    this.proofService.
      privilegesProStaCri(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.SAVE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          })
            .then((result) => {
              console.log("LIST " + this.selectedTreeList)
              this.afterCreateProof();
              this.privilegesFormSubmitted = false;
            })
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
  isObj3 = false;
  isObj4 = false;
  onInitTree(event, model) {
    if (event == undefined) {
      this.userId = 0;

      this.selectedTreeList.forEach(element => {
        if (element.id == undefined) {
          this.isObj3 = true;
          model.getNodeById(element).setIsSelected(false);
        }
      });
      if (this.isObj3 == false) {
        this.selectedTreeList.forEach(element => {
          model.getNodeById(element.id).setIsSelected(false);
        });
      }
      this.selectedTreeList = [];
      return
    }
    this.userId = event.id + ',' + this.privilegesForm.value.organizationId.id + ',' + this.privilegesForm.value.categoryId.id;
    console.log(this.userId + " ID USER")
    let params = {
      method: "GET"
    };
    this.service
      .getListCriDTOByUserId(params, this.userId)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.getListCriDTOByUserId = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          this.getListCriDTOByUserId = [];
        }
        model.expandAll();
        console.log("2- " + this.selectedTreeList)
        console.log("3- " + this.getListCriDTOByUserId);

        this.selectedTreeList.forEach(element => {
          if (element.id == undefined) {
            this.isObj4 = true;
            model.getNodeById(element).setIsSelected(false);
          }
        });
        if (this.isObj4 == false) {
          this.selectedTreeList.forEach(element => {
            model.getNodeById(element.id).setIsSelected(false);
          });
        }
        if (this.getListCriDTOByUserId.length > 0) {
          console.log("LENGTH LIST 2 > 0")
          this.getListCriDTOByUserId.forEach(element => {
            console.log(element.id + "ID ELE")
            model.getNodeById(element.id).setIsSelected(true);
          });

          this.selectedTreeList = this.getListCriDTOByUserId;
        } else {
          this.selectedTreeList = [];
        }

      })
    // .catch((error) => {
    //   Swal.close();
    //   Swal.fire({
    //   icon: "error",
    //   title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
    //   confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
    //   });
    // });
  }

  // filter
  public optionsFilter = {
    useCheckbox: true
  };

  public optionsDisabledCheckbox: ITreeOptions = {
    useCheckbox: true,
    // getChildren: this.getCheckboxChildren.bind(this),
    useTriState: true
  };

  public nodesFilter = [];
  public nodesMenuFilter = [];

  /**
  * filterFn
  *
  * @param value
  * @param treeModel
  */
  filterFn(value: string, treeModel: TreeModel) {
    treeModel.filterNodes((node: TreeNode) => fuzzysearch(value, node.data.name));
  }

  public listProgramId = [];
  public listStandatdId = [];
  public listCriteriaId = [];

  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);
    console.log("SELECTES " + this.selectedTreeList)
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