import { Component, ViewChild, OnInit, ViewEncapsulation } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import { PrivilegesManagementService } from '../privileges-management.service';
import { ThrowStmt } from '@angular/compiler';
import { CoreTranslationService } from '@core/services/translation.service';
import { TranslateService } from '@ngx-translate/core';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';


@Component({
  selector: 'app-list-privileges',
  templateUrl: './list-privileges.component.html',
  styleUrls: ['./list-privileges.component.scss'],
  encapsulation: ViewEncapsulation.None
})

export class ListPrivilegesComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public contentHeader: object;
  public dataMenus: any[];
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public dataMethod = [{id:"1", name: "DETAIL"}, {id:"2", name: "ADD"}, {id:"3", name: "UPDATE"}, {id:"4", name: "DELETE"}, {id:"5", name: "SEARCH"}, {id:"6", name: "LOCK"}];

  constructor(
    private service: PrivilegesManagementService,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService
  ) { }

  ngOnInit(): void {
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_PRI',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.ADMINISTRATOR',
            isLink: true,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.PRI_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.searchRoles();
    this.getMenus();
  }

  searchRoles(){
    let params = {
      method: "GET", keyword:this.keyword, currentPage: this.currentPage, perPage:this.perPage
    };
    Swal.showLoading();
    this.service
      .searchRoles(params)
      .then((data) => {
        Swal.close();
        let response = data;
        
        if (response.code === 0) {
          this.rows = response.content["items"];
          for(let i=0;i<this.rows.length;i++){
            for(let j=0;j<this.dataMethod.length;j++){
              if(this.rows[i].method == this.dataMethod[j].id){
                this.rows[i]["method"] = this.dataMethod[j].name;
              }
            }
          }
          
          this.totalRows = response.content["total"];
        } else {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.FIND_NOT'),
          });
          if(response.code === 2){
            this.rows = [];
            this.totalRows = 0;
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

  getMenus(){
    let params = {
      method: "GET"
    };
    Swal.showLoading();
    this.service
      .getLsMenus(params)
      .then((data) => {
        // console.log(JSON.stringify(data))
        Swal.close();
        this.dataMenus = data.content;
      
        
      })
      .catch((error) => {
        // console.log(error)
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }

  deleteRoles(id){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.DELETE_CONFIRM'),
      // text: 'Xóa quyền cha sẽ xóa luôn các quyền con',
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
              title: this._translateService.instant('MESSAGE.PRIVILEGES_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.currentPage = 0;
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

  openModalAddRoles(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editRolxes(roleId, modalSM, event){
    event.target.closest('datatable-body-cell').blur();
    window.localStorage.removeItem("roleId");
    window.localStorage.setItem("roleId", roleId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  detailPrivileges(roleId, modalSM, event) {
    event.target.closest('datatable-body-cell').blur();
    window.localStorage.removeItem("roleId");
    window.localStorage.setItem("roleId", roleId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchRoles();
  }

  afterCreateRoles(){
    this.currentPage = 0;
    this.modalService.dismissAll();
    this.searchRoles();
  }

  afterEditRoles(){
    this.currentPage = 0;
    this.modalService.dismissAll();
    this.searchRoles();
  }

  afterDetailRoles(){
    this.modalService.dismissAll();
    this.searchRoles();
  }

  changePerpage(){
    this.searchRoles();
  }


}
