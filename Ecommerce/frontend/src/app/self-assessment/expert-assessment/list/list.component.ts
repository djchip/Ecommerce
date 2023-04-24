import { TokenStorage } from './../../../services/token-storage.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DatatableComponent, ColumnMode } from '@swimlane/ngx-datatable';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import Swal from 'sweetalert2';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ExpertService } from '../expert.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public ListReportType =[{id:"1", name: "Tiêu chuẩn", nameEn: "Standard"}, 
                      {id:"2", name: "Tiêu chí", nameEn: "Criteria"}, 
                      {id:"3", name: "Tổng hợp", nameEn: "Synthetic"},
                       ];
  public perPage = 10;
  public totalRows = 0;
  public programId = null;
  public listPrograms = [];
  public idPro = "";
  public messages;
  disableSta = true;
  public listEvaluate=[{id:"0", name: "Chưa đánh giá", nameEn: "Not yet rated"}, 
                      {id:"1", name: "Đã comment", nameEn: "Commented"}, 
                      {id:"2", name: "Đã sửa", nameEn: "Fixed"},
                      {id:"3", name: "Đạt", nameEn: "Achieve"},
                      {id:"4", name: "Không đạt", nameEn: "Not achieved"}];
  public evaluated=0;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  // public url = "https://10.252.10.236:9980/browser/a4b9c74/cool.html?WOPISrc=http://10.252.10.236:3232/neo/assessment/wopi/files/";
  constructor(private service: ExpertService, private modalService: NgbModal, private tokenStorage: TokenStorage, 
    private _coreTranslationService: CoreTranslationService, private _translateService: TranslateService, 
    private _changeLanguageService:ChangeLanguageService, private router: Router) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
        // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
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
    this.getListPrograms();
    this.searchAssessment();
    console.log("Lang: " + this._translateService.currentLang);
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_ASSESSMENT_EXPERT',
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
            name: 'MENU.REPORT',
            isLink: false,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.EXPERT_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  openAssessment(id){
    let xMax = window.outerWidth;
    let yMax = window.outerHeight;
    window.open(this.service.getURLEditor(id), 'assessment', 'width='+xMax+', height='+yMax+',left=0,top=0');
  }

  searchAssessment(){
    if(this.programId == null){
      this.idPro = "";
    } else {
      this.idPro = this.programId;
    }
    let params = {
      method: "GET", 
      keyword:this.keyword,
       lang: this._translateService.currentLang,
       programId:this.idPro, 
       
       currentPage: this.currentPage,
        perPage: this.perPage,
      
    };
    console.log(this.keyword);
    
    Swal.showLoading();
    this.service
      .searchAssessment(params)
      .then((data) =>{
        Swal.close();
        let response:any = data;
        // console.log(data);
        
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
  onChange(){
    this.searchAssessment();
    console.log("tranin: ",this.evaluated);
    
  }
  reset(){
    this.evaluated=0;
    this.searchAssessment;
  }
  editAssessment(id, modalSM){
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });
  }

  detailAssessment(id, modalSM){
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });
  }

  deleteAssessment(id:number){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.ASSESSMENT.DELETE_CONFIRM'),
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
        this.service.deleteAssessment(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.ASSESSMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchAssessment();
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

  afterCreateAssessment(){
    this.modalService.dismissAll();
    this.searchAssessment();
  }

  afterEditAssessment(){
    this.modalService.dismissAll();
    this.searchAssessment();
  }

  openModalAddAssessment(modalSM){
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  onChangePro(){
 
    this.disableSta = false;
    this.searchAssessment();
  }

  resetPro(){
    this.programId = null;
    // this.listStandard;
    this.searchAssessment();
    this.disableSta = true;
  }

  getListPrograms(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
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

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchAssessment();
  }

  changePerpage(){
    this.currentPage = 0
    this.searchAssessment();
  }

}
