import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import Swal from "sweetalert2";

import { TranslateService } from '@ngx-translate/core';
import { RolesGroupManagementService } from '../../roles-group-management/roles-group-management.service'
import { AuthenticationManagementService } from '../authentication-management.service'
import * as snippet from 'app/main/extensions/tree-view/tree-view.snippetcode';
import { TreeModel, TreeNode } from '@circlon/angular-tree-component';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { Router } from '@angular/router';
import { TokenStorage } from 'app/services/token-storage.service';

@Component({
  selector: 'app-edit-authentication',
  templateUrl: './edit-authentication.component.html',
  styleUrls: ['./edit-authentication.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class EditAuthenticationComponent implements OnInit {
  public isAdmin = false;
  public listUserId = [];
  public currentUserName;

  public listRole = [];
  public listPrivilegesByRole = [];
  public roleLoading = false;
  public contentHeader: object;
  public _snippetCodeFilter = snippet.snippetCodeFilter;
  public role;
  public submitSave = false;
  public isInvalidRole = true;
  public isInvalidPrivileges = true;
  public isInvalidMenu = true;
  public listMenuByRole = [];

  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;

  constructor(private tokenStorage: TokenStorage,
    public _translateService: TranslateService,
    private router: Router,
    public service: AuthenticationManagementService,
    public roleService: RolesGroupManagementService,
    private _coreTranslationService: CoreTranslationService
  ) { }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    this.currentUserName = this.tokenStorage.getUsername();
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
            name: 'CONTENT_HEADER.ADMINISTRATOR',
            isLink: false,
            link: '/'
          },
          {
            name: 'MENU.AUTH_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this.getUserIdByRolesid();

    this.getListRole();
    this.getTree();
    // this.getTreeMenu();
  }

  getUserIdByRolesid() {
    let params = { method: "GET" }
    this.service
      .getUserIdByRolesid(params, this.currentUserName)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUserId = response.content;
          console.log(this.listUserId+ " = list user");
          
          if (this.listUserId.length > 0) {
            this.listUserId.forEach((item) => {
              if (item == 1) {
                this.isAdmin = true;
              }

            })
          }
        } else {
          this.listUserId = [];
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      })
  }

  getListRole() {
    let params = {
      method: "GET"
    };
    this.roleService
      .getListRoles(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listRole = response.content;
          
          this.listRole.forEach((item, index)=>
          {
            if (this.isAdmin) {
              if (item.roleCode === "Super Admin") {
                this.listRole.splice(index ,1);
              }
          }
          })
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if (response.code === 2) {
            this.listRole = [];
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

  getTree() {
    let params = {
      method: "GET"
    };
    this.service
      .getTree(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.nodesFilter = response.content;
          console.log('Hihi ' + this.nodesFilter);
        } else {
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          // title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          // confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }
  getTreeMenu() {
    let params = {
      method: "GET"
    };
    this.service
      .getListMenu(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.nodesMenuFilter = response.content;
          console.log('Hihi ' + this.nodesMenuFilter);
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

  // filter
  public optionsFilter = {
    useCheckbox: true
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
  public selectedTreeList = [];
  public selectedTreeMenu = [];

  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);

  }
  onselectMenu(event) {
    this.selectedTreeMenu = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);
  }
  //   callAll((event, model)){
  // this.onInitTree((event, model);
  // this.onInitTree1((event, model));

  //   }
  callAllUpdate() {
    this.updatePrivileges();
    // this.updateRoleMenu();
  }
  // onInitTreeMenu(event, model) {
  //   this.isInvalidRole = false;
  //   if (event == undefined) {
  //     this.role = 0;
  //     this.selectedTreeMenu.forEach(element => {
  //       model.getNodeById(element).setIsSelected(false);
  //     });
  //     this.selectedTreeMenu = [];
  //     return
  //   }
  //   this.role = event.id;
  //   let params = {
  //     method: "GET"
  //   };
  //   this.service
  //     .getMenuByRoleId(params, this.role)
  //     .then((data) => {
  //       let response = data;
  //       if (response.code === 0) {
  //         this.listMenuByRole = response.content;
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: response.errorMessages,
  //         });
  //         console.log("log 1");

  //         this.listMenuByRole = [];
  //       }
  //       model.expandAll();
  //       this.selectedTreeMenu.forEach(element => {
  //         model.getNodeById(element).setIsSelected(false);
  //       });

  //       if (this.listMenuByRole.length > 0) {
  //         console.log("log check");
  //         this.listMenuByRole.forEach(element => {
  //           model.getNodeById(element).setIsSelected(true);
  //         });

  //         this.selectedTreeMenu = this.listMenuByRole;
  //         console.log("selected menu");

  //         console.log(this.selectedTreeMenu);

  //         this.isInvalidMenu = false;
  //       } else {
  //         console.log("log else");
  //         this.selectedTreeMenu = [];
  //         this.isInvalidMenu = true;
  //       }

  //     })
  //     .catch((error) => {
  //       console.log("oooo");
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });
  // }

  onInitTree(event, model) {
    console.log(typeof(event) + "BBB")
    this.isInvalidRole = false;
    if (event == undefined) {
      this.role = 0;
      this.selectedTreeList.forEach(element => {
        model.getNodeById(element).setIsSelected(false);
      });
      this.selectedTreeList = [];
      return
    }
    this.role = event.id;
    console.log("role" + event.id)
    let params = {
      method: "GET"
    };
    this.service
      .getPrivilegesByRoleId(params, this.role)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listPrivilegesByRole = response.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          this.listPrivilegesByRole = [];
        }
        console.log("this.selectedTreeList" + this.selectedTreeList)
        model.expandAll();
        this.selectedTreeList.forEach(element => {
          model.getNodeById(element).setIsSelected(false);
        });
        if (this.listPrivilegesByRole.length > 0) {
          this.listPrivilegesByRole.forEach(element => {
            // console.log(model.getNodeById(element).data.id + "NODE")
            console.log(element + "ID ELE")
            model.getNodeById(element).setIsSelected(true);
          });

          this.selectedTreeList = this.listPrivilegesByRole;
          this.isInvalidPrivileges = false;
        } else {
          this.selectedTreeList = [];
          this.isInvalidPrivileges = true;
        }

      })
      .catch((error) => {
        // Swal.close();
        // Swal.fire({
        // icon: "error",
        // title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
        // confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        // });
      });
  }

  updatePrivileges() {
    this.submitSave = true;
    if (this.role == undefined || this.role == 0) {
      this.isInvalidRole = true;
    } else {
      this.isInvalidRole = false;
    }
    if (this.selectedTreeList.length == 0) {
      this.isInvalidPrivileges = true;
    } else {
      this.isInvalidPrivileges = false;
    }

    if (this.isInvalidRole || this.isInvalidPrivileges) {
      return
    }

    let params = {
      method: "POST",
      content: { roleId: this.role, privilegesId: this.selectedTreeList },
    };
    Swal.showLoading();
    this.service
      .updatePrivileges(params)
      .then((data) => {
        Swal.close();
        let response = data;
        console.log('Content ' + this.selectedTreeList);
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.AUTHENTICATION_MANAGEMENT.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
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
  updateRoleMenu() {
    this.submitSave = true;
    if (this.role == undefined || this.role == 0) {
      this.isInvalidRole = true;
    } else {
      this.isInvalidRole = false;
    }
    if (this.selectedTreeMenu.length == 0) {
      this.isInvalidMenu = true;
    } else {
      this.isInvalidMenu = false;
    }

    if (this.isInvalidRole || this.isInvalidMenu) {
      return
    }
    console.log("list là");

    console.log(this.selectedTreeMenu);


    let params = {
      method: "POST",
      content: { roleId: this.role, menuId: this.selectedTreeMenu },
    };
    Swal.showLoading();
    this.service
      .updateRoleMenu(params)
      .then((data) => {
        Swal.close();
        let response = data;
        console.log('Content ' + this.selectedTreeMenu);
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.AUTHENTICATION_MANAGEMENT.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
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
