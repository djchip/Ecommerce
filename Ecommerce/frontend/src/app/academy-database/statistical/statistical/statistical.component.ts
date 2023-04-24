import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { CoreTranslationService } from '@core/services/translation.service';
import { TranslateService } from '@ngx-translate/core';
import { StatisticalService } from './../statistical.service';
import { ColumnMode, DatatableComponent, SelectionType } from '@swimlane/ngx-datatable';
import { Component, OnInit, ViewEncapsulation, ViewChild } from '@angular/core';
import Swal from 'sweetalert2';
import { formatDate } from '@angular/common';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { Router } from '@angular/router';

@Component({
  selector: 'app-statistical',
  templateUrl: './statistical.component.html',
  styleUrls: ['./statistical.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class StatisticalComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  emittedEvents($event) {
    // console.log('Action : ', $event);
  }

  public contentHeader: object;
  public keyword = "";
  public rows = [];
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public disabled = true;
  public selected = [];
  public chkBoxSelected = [];
  public SelectionType = SelectionType;

  public tempForm = [];
  public rowsForm = [];
  public tempDataForm = this.rowsForm;
  public ColumnMode = ColumnMode;
  public currentPageForm = 0;
  public perPageForm = 10;
  public totalRowsForm = 0;

  public tempDocument = [];
  public rowsDocument = [];
  public tempDataDocument = this.rowsDocument;
  public currentPageDocument = 0;
  public perPageDocument = 10;
  public totalRowsDocument = 0;
  public isTitle = true;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  // [messages]='{ emptyMessage:"Không có dữ liệu", totalMessage:"bản ghi"}'
  
  public currentLang = this._translateService.currentLang;
  public messages;
  public dateFormat = window.localStorage.getItem("dateFormat");

  constructor(private service: StatisticalService, private router: Router, private _translateService: TranslateService, private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService, private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
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
    this.searchReport();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.STATISTICAL',
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
            name: 'MENU.DATABASE',
            isLink: false,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.STATISTICAL',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if (this.selected.length > 0) {
      this.disabled = false;
    } else {
      this.disabled = true;
    }
  }

  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }

  searchReport(){
    let params = {
      method: "GET", keyword: this.keyword, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .doSearch(params)
      .then((data) =>{
        Swal.close();
        let response:any = data;
        
        if (response.code === 0) {
          this.rows = response.content["items"];
          this.rows.forEach((item) =>{
            this.rowsForm = item.items;
            // console.log(this.rowsForm);
          })
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

  deleteReport(id: number){
    Swal.fire({
      title: this._translateService.instant("MESSAGE.REPORT.DELETE_CONFIRM"),
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
        let params = {
          method: "DELETE",
        };
        this.service
          .doRetrieve(id, params,)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant("MESSAGE.REPORT.DELETE_SUCCESS"),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.searchReport();
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
              title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
              confirmButtonText:
                this._translateService.instant("ACTION.ACCEPT"),
            });
          });
      }
    });
  }

  export(id, name){
    Swal.showLoading();
    let date = new Date();
    let fileName = name + "_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
    this.service.export(fileName, id).then((data) => {
      Swal.close();
    }).catch((error) => {
      Swal.close();
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      });
    })
  }

  exportForm(id){
    Swal.showLoading();
    let date = new Date();
    let fileName = "BM_" + id + "_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
    this.service.exportForm(fileName, id).then((data) => {
      console.log(data);
      Swal.close();
    }).catch((error) => {
      Swal.close();
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      });
    });
    
  }

  exportReport(modalSM){
    this.modalService.open(modalSM, {
      size: 'lg', // size: 'xs' | 'sm' | 'lg' | 'xl'
      centered: true,
    });
  }

  openModalAddReport(modalSLCIM){
    this.isTitle = true;
    window.localStorage.removeItem("ISR");
    this.modalService.open(modalSLCIM, {
      size: 'xl', // size: 'xs' | 'sm' | 'lg' | 'xl'
      scrollable: true,
    });
  }

  afterSaveReport(){
    this.modalService.dismissAll();
    this.searchReport();
    window.localStorage.removeItem("ISR");
  }

  editReport(id, modalSLCIM){
    this.isTitle = false
    window.localStorage.removeItem("ISR");
    window.localStorage.setItem("ISR", id);
    this.modalService.open(modalSLCIM, {
      size: 'xl', // size: 'xs' | 'sm' | 'lg' | 'xl'
      scrollable: true,
    });
  }

  copyReport(id, modalSLCIM){
    window.localStorage.removeItem("ISR");
    window.localStorage.setItem("ISR", id);
    this.modalService.open(modalSLCIM, {
      size: 'xl', // size: 'xs' | 'sm' | 'lg' | 'xl'
      scrollable: true,
    });
  }

  detailReport(id, modalSM){
    window.localStorage.removeItem("ISR");
    window.localStorage.setItem("ISR", id);
    this.modalService.open(modalSM, {
      size: 'lg', // size: 'xs' | 'sm' | 'lg' | 'xl'
      centered: true,
    });
  }


  onClose(){
    window.localStorage.removeItem("ISR");
  }

  onDownloadDocument(){
    let date = new Date();
    let fileName = "VanBan_" + formatDate(date, "dd_MM_yyyy_hh_mm_ss", 'en-US') + ".pdf";
  }

  onDownload(id, fileName){

  }

  onDownloadForm(){

  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchReport();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchReport();
  }

}
