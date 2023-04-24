import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent, SelectionType } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { UserManagementService } from '../user-management.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ChangeLanguageService } from 'app/services/change-language.service'
import { FormGroup, FormBuilder } from '@angular/forms';
import { TokenStorage } from 'app/services/token-storage.service';
import { Console } from 'console';

@Component({
  selector: 'app-list-user',
  templateUrl: './list-user.component.html',
  styleUrls: ['./list-user.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListUserComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  // public listRole = [{id:"1", name: "Admin"}, {id:"2", name: "Normal User"}];
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public contentHeader: object;
  public listRole = [];
  public listUnit = [];


  public listUserId = [];
  public isAdmin = false;
  public currentUserName;

  public selectedRole = -1;
  public selectedUnit = -1;
  public selected = [];
  public chkBoxSelected = [];
  public SelectionType = SelectionType;
  public deleted = true;
  public roleAdmin = window.localStorage.getItem("ADM");
  public data;
  public Roles = [];
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;


  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public proForm: FormGroup;
  constructor(private tokenStorage: TokenStorage, private formBuilder: FormBuilder, private router: Router, private service: UserManagementService, private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService, public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
      this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
    });
  }

  ngOnInit(): void {
    //check privileges
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    // content header
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_USER',
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
            name: 'CONTENT_HEADER.USER_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this.proForm = this.formBuilder.group({
      UserInfo: ['']
    })
    this.currentUserName = this.tokenStorage.getUsername();
    this._coreTranslationService.translate(eng, vie);
    this.searchUser();
    this.getListRole();
    this.getListUnit();
    this.getUserIdByRolesid();
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  addUser() {
    this.router.navigate(["/admin/user/add-user"]);
  }

  editUser(userId, modalSM) {
    window.localStorage.removeItem("userId");
    window.localStorage.setItem("userId", userId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteUser(id: number) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.DELETE_CONFIRM'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let params = {
          method: 'DELETE'
        }
        this.service.deleteUser(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchUser();
            });
          } else if (response.code === 45) {
            Swal.fire(this._translateService.instant('MESSAGE.USER_MANAGEMENT.NOT_DELETED'));
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
              // title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
              title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.NOT_DELETED'),

              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            });
          });
      }
    });
  }

  idDetail
  ObjDetail
  detailUser(user, modalSM) {
    this.idDetail = user.id;
    this.ObjDetail = user;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }


  lockUser(id: number) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.LOCK_CONFIRM'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let params = {
          method: 'PUT'
        }
        this.service.lockUser(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.LOCK_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchUser();
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
    });
  }

  unlockUser(id: number) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.UNLOCK_CONFIRM'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let params = {
          method: 'PUT'
        }
        this.service.unLockUser(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.UNLOCK_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchUser();
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
    });
  }

  getUserIdByRolesid() {
    let params = { method: "GET" }
    this.service
      .getUserIdByRolesid(params, this.currentUserName)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUserId = response.content;
          console.log("listUserId =" + this.listUserId);

          if (this.listUserId.length > 0) {
            this.listUserId.forEach((item) => {
              if (item == 1 && item != 121) {
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


  searchUser() {
    let params = {
      method: "GET", keyword: this.keyword, roleId: this.selectedRole, unitId: this.selectedUnit, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchUser(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {

          for (let j = 0; j < data.content["items"].length; j++) {
            this.Roles = data.content["items"][j].role;
            for (let k = 0; k < this.Roles.length; k++) {
              let codeRole = this.Roles[k].roleCode;
              //  TH1: tk là admin
              if (codeRole === "ADMIN" || codeRole === "Super Admin") {
                data.content["items"][j].password = true;
              }
              // TH2: tk là admin cấp cao
              if (codeRole === "Super Admin") {
                data.content["items"][j].undoStatus = true;
              }
            }
          }
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];
          // console.log(this.rows);
          
        } else {
          if (response.code === 2) {
            this.rows = [];
            this.totalRows = 0;
          }
        }
        // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
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

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchUser();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchUser();
  }

  // modal Open Small
  openModalAddUser(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterCreateUser() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchUser();
  }

  afterEditUser() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchUser();
  }

  getListRole() {
    let params = {
      method: "GET"
    };
    this.service
      .getListRole(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listRole = response.content;


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

  onSelectRole(event) {
    if (event != undefined) {
      this.selectedRole = event.id;
    } else {
      this.selectedRole = -1;
    }
    this.searchUser();
  }

  onSelectUnit(event) {
    if (event != undefined) {
      this.selectedUnit = event.id;
    } else {
      this.selectedUnit = -1;
    }
    this.searchUser();
  }

  onDeleteMulti() {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.DELETE_CONFIRM'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
      customClass: {
        confirmButton: 'btn btn-primary',
        cancelButton: 'btn btn-danger ml-1'
      }
    }).then((result) => {
      if (result.value) {
        let arr = [];
        for (let [key, value] of Object.entries(this.selected)) {
          arr.push(value['id']);
        }
        let dto = this.proForm.value;
        dto.UserInfo = arr;
        let params = {
          method: 'DELETE', content: dto.UserInfo,
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.deleted= true;
              this.searchUser();
            });
          } else if (response.code === 12) {
            Swal.fire(this._translateService.instant('MESSAGE.USER_MANAGEMENT.NOT_DELETED'));
          } else if (response.code === 107) {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('hu'),
              // confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            });
          } else {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.PROGRAM_CANNOT_DELETE'),
            });
          }
        })
          .catch((error) => {
            Swal.close();
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.PROGRAM_CANNOT_DELETE1'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            });
          });
      }
    });
  }
  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }
  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if (this.selected.length > 0) {
      this.deleted = false;
    } else {
      this.deleted = true;
    }

  }
}
