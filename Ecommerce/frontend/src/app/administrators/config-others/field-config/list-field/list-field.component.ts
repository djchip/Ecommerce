import { Component, OnInit } from '@angular/core';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../../config.service';
import { CoreTranslationService } from '@core/services/translation.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';

@Component({
  selector: 'app-list-field',
  templateUrl: './list-field.component.html',
  styleUrls: ['./list-field.component.scss']
})
export class ListFieldComponent implements OnInit {

  public contentHeader: object;
  public currentLang = this._translateService.currentLang;
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;

  constructor(
    private router: Router,
    private service: ConfigService,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService,
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;

    });
  }

  ngOnInit(): void {
    this.searchField();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.CONFIG_OTHERS',
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
            isLink: false,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.CONFIG_OTHERS',
            isLink: false
          },
          {
            name: 'CONTENT_HEADER.FIELD_CONFIG',
            isLink: false
          }
        ]
      }
    };
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchField();
  }

  changePerpage() {
    this.searchField();
  }

  openModalAddField(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  afterCreateField(){
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchField();
  }

  afterEditField(){
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchField();
  }

  editField(id, modalSM){
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteField(id:number){
    Swal.fire({
      title: this._translateService.instant('LABEL_APP_PARAM.DELETE_FIELD_CONFIRM'),
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
        this.service.deleteDocumentType(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('LABEL_APP_PARAM.DELETE_FIELD_SUCCES'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchField();
            });
          } else if (response.code === 12) {
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant("LABEL_APP_PARAM.CANNOT_DELETE_FIELD"),
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

  searchField() {
    let params = {
      method: "GET",
      keyword: this.keyword,
      currentPage: this.currentPage,
      perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchField(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          for (let i = 0; i < data.content["items"].length; i++) {
            if (data.content["items"][i]["updatedBy"] == '' || data.content["items"][i]["updatedBy"] == null) {
              data.content["items"][i]["updatedDate"] = '';
            } else {
              let updatedDate = data.content["items"][i]["updatedDate"];
              data.content["items"][i]["updatedDate"] = updatedDate.split("T")[0].split("-")[2] + "-" + updatedDate.split("T")[0].split("-")[1] + "-" + updatedDate.split("T")[0].split("-")[0];
            }
            let createdDate = data.content["items"][i]["createdDate"];
            data.content["items"][i]["createdDate"] = createdDate.split("T")[0].split("-")[2] + "-" + createdDate.split("T")[0].split("-")[1] + "-" + createdDate.split("T")[0].split("-")[0];
          }
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];
          document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;


        } else {
          if (response.code === 2) {
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
          confirmButtonText: "OK",
        });
      });
  }

}
