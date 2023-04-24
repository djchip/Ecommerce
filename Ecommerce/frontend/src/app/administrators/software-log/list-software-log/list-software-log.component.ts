import { SoftwareLogModule } from './../software-log.module';
import { DetailSoftwareLogComponent } from './../detail-software-log/detail-software-log.component';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { NgbCalendar, NgbDate, NgbModal, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { Router, Resolve } from '@angular/router';
import { ColumnMode, DatatableComponent ,SelectionType} from '@swimlane/ngx-datatable';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { formatDate } from '@angular/common';
import Swal from 'sweetalert2';
import { SoftwareLogService } from '../software-log.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-list-software-log',
  templateUrl: './list-software-log.component.html',
  styleUrls: ['./list-software-log.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListSoftwareLogComponent implements OnInit {
  @ViewChild(DatatableComponent) table: DatatableComponent;

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public deleted=true;
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public chkBoxSelected = [];

  public totalRows = 0;
  public listErrorLog;
  public oker0;
  public oker;
  public CustomDayDPdata: NgbDateStruct;
  public EndDayDPdata: NgbDateStruct;
  valueFromDate: any;
  valueFromToDate: any;
  public selected = [];
  public proForm: FormGroup;
  public SelectionType = SelectionType;
  isWeekend = (date: NgbDate) => this.calendar.getWeekday(date) >= 6;
  isDisabled = (date: NgbDate, current: { month: number; year: number }) =>
    date.month !== current.month;
  // disableSta = true;
  public status = null;
  public listStatus = [{id:"0", nameEn: "Bug", name: "Lỗi"}, 
                      {id:"1", nameEn: "Fixed", name: "Đã sửa"}, 
                      {id:"2", nameEn: "Closed", name: "Đã đóng"},
                      {id:"3", nameEn: "Cancel", name: "Làm lại"}];
  public idStatus = "";
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  // public hoveredDate: NgbDate | null = null;
  // public fromDate: NgbDate | null;
  // public toDate: NgbDate | null;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;
  constructor(private formBuilder: FormBuilder,private service: SoftwareLogService, private router: Router, private modalService: NgbModal, 
    private _coreTranslationService: CoreTranslationService, private _translateService: TranslateService, private calendar: NgbCalendar, private _changeLanguageService: ChangeLanguageService) { 
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
    this.searchSoftwareLog();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_SOFTWARE_LOG',
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
            name: 'CONTENT_HEADER.SOFTWARE_LOG',
            isLink: false
          }
        ]
      }
    };
    this.proForm = this.formBuilder.group({
      roles: ['']
    })
    this._coreTranslationService.translate(eng, vie);
    this.listStatus;
    this.searchSoftwareLog();
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }
searchSoftwareLog() {
  if(this.status == null){
    this.idStatus = "";
  } else {
    this.idStatus = this.status;
  }
  let params = {
    method: "GET", 
    keyword:this.keyword, 
    currentPage: this.currentPage, 
    perPage: this.perPage, 
    status: this.idStatus,
    startDate: this.valueFromDate ? this.valueFromDate : "",
    endDate: this.valueFromToDate ? this.valueFromToDate : "", 
  };
  Swal.showLoading();
  this.service
    .searchSoftwareLog(params)
    .then((data) =>{
      Swal.close();
      let response:any = data;
      console.log(data);
      
      if (response.code === 0) {
        // for(let i = 0; i< data.content["items"].length ; i++){
        //   let a = data.content["items"][i]["errorlogtime"];
        //   let b = data.content["items"][i]["successfulrevisiontime"];
        //   data.content["items"][i]["errorlogtime"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0] 
        //   + " | " + a.split("T")[1].split(":")[0] + ":" + a.split("T")[1].split(":")[1] + ":" + a.split("T")[1].split(":")[1];
        //   data.content["items"][i]["successfulrevisiontime"] = b.split("T")[0].split("-")[2] + "-" + b.split("T")[0].split("-")[1] + "-" + b.split("T")[0].split("-")[0] 
        //   + " | " + b.split("T")[1].split(":")[0] + ":" + b.split("T")[1].split(":")[1] + ":" + b.split("T")[1].split(":")[1];
        // }
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
editSoftwareLog(id, modalSM) {
  window.localStorage.removeItem("id");
  window.localStorage.setItem("id", id);
  this.modalService.open(modalSM, {
    centered: true,
    size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
  });
}
deleteSoftwareLog(id) {
  Swal.fire({
    title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.DELETE_CONFIRM'),
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy',
    customClass: {
      confirmButton: 'btn btn-primary',
      cancelButton: 'btn btn-danger ml-1'
    }
  }).then((result) => {
    if (result.value) {
      let params = {
        method: 'DELETE'
      }
      this.service.deleteSoftwareLog(params, id).then((data) => {
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.DELETE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            //load lại trang kết quả
            this.searchSoftwareLog();
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
undo(id:number){
  Swal.fire({
    title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.UNDO_CONFIRM'),
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
      this.service.undo(params, id).then((data) => {
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.UNDO_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.searchSoftwareLog();
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
setPage(pageInfo) {
  this.currentPage = pageInfo.offset;
  this.searchSoftwareLog();
}
onChange() {
  this.searchSoftwareLog();
}
changePerpage(){
  this.currentPage = 0;
  this.searchSoftwareLog();
}
openModalAddMenus(modalSM) {
  this.modalService.open(modalSM, {
    centered: true,
    size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
  });
}

idDetail;
ObjDetail;
detailSoftwareLog(SoftwareLog, modalSM){
  this.idDetail = SoftwareLog.id;
  this.ObjDetail = SoftwareLog;
  // window.localStorage.removeItem("id");
  // window.localStorage.setItem("id", id);
  this.modalService.open(modalSM, {
    centered: true,
    size: 'lg'
  })
}
changeDate(value, type){
  if(type == 'startDate'){
    this.valueFromDate = this.convertDate(value);
  }else if(type == 'endDate'){
    this.valueFromToDate = this.convertDate(value);
  }
  this.currentPage = 0;
  this.searchSoftwareLog();
}

convertDate(value: any): any {
  var date = value.year + "-" + value.month + "-" + value.day;
  return date;
}
afterCreateSoftwareLog() {
  this.modalService.dismissAll();
  this.currentPage = 0
  this.searchSoftwareLog();
}
// afterCreateVersion() {
//   this.modalService.dismissAll();
//   this.currentPage = 0
// }
afterEditSoftwareLog() {
  this.modalService.dismissAll();
  this.currentPage = 0
  this.searchSoftwareLog();
}
onChange1(){
  // alert(this.oker);
  // alert(this.oker0);
  if(this.oker == null && this.oker0 == null){
    if(this.status == null){
      this.idStatus = "";
    } else {
      this.idStatus = this.status;
    }
    let params = {
      method: "GET", 
      keyword:this.keyword, 
      currentPage: this.currentPage, 
      perPage: this.perPage, 
      status: this.idStatus,
      startDate: "",
      endDate:  "", 
    };
    Swal.showLoading();
    this.service
      .searchSoftwareLog(params)
      .then((data) =>{
        Swal.close();
        let response:any = data;
        console.log(data);
        
        if (response.code === 0) {
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];
        } else {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.FIND_NOT'),
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


}

onDeleteMulti(){
  Swal.fire({
    title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.DELETE_CONFIRM'),
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
            title: this._translateService.instant('MESSAGE.SOFTWARE_LOG.DELETE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            //load lại trang kết quả
            this.deleted= true;
            this.searchSoftwareLog();
          });
        } else if (response.code === 12) {
          Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.BEING_USED'));
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
