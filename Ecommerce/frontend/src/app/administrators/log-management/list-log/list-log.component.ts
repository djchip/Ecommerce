import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { LogService } from './../log.service';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { formatDate } from '@angular/common';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-list-log',
  templateUrl: './list-log.component.html',
  styleUrls: ['./list-log.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListLogComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;

  constructor(private service: LogService, private router: Router, private modalService: NgbModal, 
    private _coreTranslationService: CoreTranslationService, private _translateService: TranslateService) { }

  ngOnInit(): void {
    this.searchLog();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_LOG',
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
            name: 'CONTENT_HEADER.LOG_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
  }

  searchLog(){
    let params = {
      method: "GET", keyword:this.keyword, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchLog(params)
      .then((data) =>{
        Swal.close();
        let response:any = data;
        if (response.code === 0) {
          for(let i = 0; i< data.content["items"].length ; i++){
            let a = data.content["items"][i]["actionTime"];
            data.content["items"][i]["actionTime"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0] 
            + " | " + a.split("T")[1].split(":")[0] + ":" + a.split("T")[1].split(":")[1] + ":" + a.split("T")[1].split(":")[2];
          }
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];
        } else {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.LOG_MANAGEMENT.FIND_NOT'),
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

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchLog();
  }

  changePerpage(){
    this.searchLog();
  }

  detailLog(id, modalSM){
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    })
  }

  onExportToExcel(){
    let date = new Date();
    let fileName = "Log_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
    this.service.export(fileName, "excel");
  }
}
