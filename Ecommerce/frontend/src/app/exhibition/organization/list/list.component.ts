import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent, SelectionType } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { OrganizationService } from '../organization.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListComponent implements OnInit {

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
  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;
  public selected = [];
  public deleted = true;
  public chkBoxSelected = [];
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  
  public SelectionType = SelectionType;
  constructor(private _changeLanguageService: ChangeLanguageService, private router: Router, private service: OrganizationService, private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService, public _translateService: TranslateService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
      this.search();
      this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
    })
  }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    // content header
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_ORG',
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
            name: 'CONTENT_HEADER.LIST_ORG',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.search();
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  addUser() {
    this.router.navigate(["/admin/user/add-user"]);
  }

  editUser(userId, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", userId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteUser(id: number) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.DELETE_CONFIRMOR'),
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
              title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.ORGANIZATION_DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.search();
            });
          }
          else {
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
  idDetail;
  ObjDetail;
  detailUser(organization, modalSM) {
    this.idDetail = organization.id;
    this.ObjDetail = organization;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
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

  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }

  onDeleteMulti() {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.ORG.DELETE_CONFIRM"),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
      cancelButtonText: this._translateService.instant("ACTION.CANCEL"),
      customClass: {
        confirmButton: "btn btn-primary",
        cancelButton: "btn btn-danger ml-1",
      },
    }).then((result) => {
      if (result.value) {
        let arr = [];
        this.selected.map((e) => {
          arr.push(e["id"]);
        });
        let params = {
          method: "DELETE",
          content: arr,
        };
        this.service
          .deleteMulti(params)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant(
                  "MESSAGE.PROGRAM_MANAGEMENT.ORGANIZATION_DELETE_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.deleted= true;
                this.search();
              });
              // } else if (response.code === 12) {
              //   Swal.fire(
              //     this._translateService.instant(
              //       "MESSAGE.PROGRAM_MANAGEMENT.BEING_USED_ASSESSMENT"
              //     )
              //   );
            } else {
              Swal.fire({
                icon: "error",
                title: this._translateService.instant(
                  "MESSAGE.ORG.CANNOT_DELETE"
                ),
              });
            }
          })
          .catch((error) => {
            Swal.close();
            Swal.fire({
              icon: "error",
              title: this._translateService.instant(
                "MESSAGE.COMMON.CONNECT_FAIL"
              ),
              confirmButtonText:
                this._translateService.instant("ACTION.ACCEPT"),
            });
          });
      }
    });
  }
  search() {
    let params = {
      method: "GET", keyword: this.keyword, lang: this._translateService.currentLang, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .search(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          // for(let i = 0; i< data.content["items"].length ; i++){
          //   let a = data.content["items"][i]["updatedDate"];

          //   data.content["items"][i]["updatedDate"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0];
          // }
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];

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
    this.search();
  }

  changePerpage() {
    this.currentPage = 0
    this.search();
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
    this.search();
  }

  afterEdit() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.search();
  }

}
