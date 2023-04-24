import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent,SelectionType } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { RolesGroupManagementService } from '../roles-group-management.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ChangeLanguageService } from 'app/services/change-language.service'
import { FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-list-roles-group',
  templateUrl: './list-roles-group.component.html',
  styleUrls: ['./list-roles-group.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListRolesGroupComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public listRole = [{id:"1", name: "Admin"}, {id:"2", name: "Normal User"}];
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public proForm: FormGroup;
  public chkBoxSelected = [];

  public totalRows = 0;
  public deleted = true;
  public roleAdmin = window.localStorage.getItem("ADM");
  public contentHeader: object;
  public selected = [];
  public messages;
  public SelectionType = SelectionType;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;

  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  constructor(private formBuilder: FormBuilder,private router: Router, private service: RolesGroupManagementService, private modalService: NgbModal,
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
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    // content header
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_ROLES_GROUP',
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
            name: 'CONTENT_HEADER.ROLES_GROUP',
            isLink: false
          }
        ]
      }
    };
    this.proForm = this.formBuilder.group({
      roles: ['']
    })
    this._coreTranslationService.translate(eng, vie);
    this.searchRoles();
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }
  addRoles(){
    this.router.navigate(["/administrators/roles-group-management/add-roles-group"]);
  }

  editRoles(roleId, modalSM){
    window.localStorage.removeItem("roleId");
    window.localStorage.setItem("roleId", roleId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteRoles(id:number){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.ROLES_GROUP.DELETE_CONFIRM'),
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
        this.service.deleteRoles(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.ROLES_GROUP.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchRoles();
            });
          } else if(response.code === 6){
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.ROLES_GROUP.ROLE_ALREADY_USED'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            });
          }else {
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
idDetail
objdetail
  detailRoles(role, modalSM){
    this.idDetail=role.id;
    this.objdetail=role;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }


  lockRoles(id:number){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.ROLES_GROUP.LOCK_CONFIRM'),
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
        this.service.lockRoles(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.ROLES_GROUP.LOCK_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchRoles();
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

  unlockRoles(id:number){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.ROLES_GROUP.UNLOCK_CONFIRM'),
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
        this.service.unLockRoles(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.ROLES_GROUP.UNLOCK_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchRoles();
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

  searchRoles(){
    let params = {
      method: "GET", keyword:this.keyword, currentPage: this.currentPage, perPage:this.perPage
    };
    Swal.showLoading();
    this.service
      .addRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];

        } else {
          if(response.code === 2){
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
    this.searchRoles();
  }

  changePerpage(){
    this.currentPage = 0
    this.searchRoles();
  }

  // modal Open Small
  openModalAddRoles(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterCreateRoles(){
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchRoles();
  }

  afterEditRoles(){
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchRoles();
  }

 
  onDeleteMulti(){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.ROLES_GROUP.DELETE_CONFIRM'),
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
        for(let [key, value] of Object.entries(this.selected)){
          arr.push(value['id']);
        }
        let dto = this.proForm.value;
        dto.roles = arr;
        let params = {
          method: 'DELETE', content: dto.roles,
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.ROLES_GROUP.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchRoles();
            });
          } else if (response.code === 12) {
            Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.BEING_USED1'));
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
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
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
    if(this.selected.length > 0){
      this.deleted = false;
      console.log("oke "+ this.deleted.valueOf)

    } else {
      this.deleted = true;
      console.log("oke 5"+ this.deleted.valueOf)

    }

  }

}
