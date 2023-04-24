import { LogUndoService } from "./log-undo.service";
import { TranslateService } from "@ngx-translate/core";
import { CoreTranslationService } from "./../../../@core/services/translation.service";
import {
  NgbCalendar,
  NgbDate,
  NgbDateStruct,
  NgbModal,
} from "@ng-bootstrap/ng-bootstrap";
import { ColumnMode, DatatableComponent } from "@swimlane/ngx-datatable";
import { Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import Swal from "sweetalert2";
import { ChangeLanguageService } from "app/services/change-language.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-log-undo",
  templateUrl: "./log-undo.component.html",
  styleUrls: ["./log-undo.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class LogUndoComponent implements OnInit {
  public contentHeader: object;
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  listAction = [];
  tableName = null;
  action = null;
  createdBy = null;
  isWeekend = (date: NgbDate) => this.calendar.getWeekday(date) >= 6;
  isDisabled = (date: NgbDate, current: { month: number; year: number }) =>
    date.month !== current.month;
  public CustomDayDPdata: NgbDateStruct;
  public EndDayDPdata: NgbDateStruct;
  valueFromDate: any;
  valueFromToDate: any;
  public currentLang = this._translateService.currentLang;
  public objID;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public oker;
  public oker0;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  @ViewChild(DatatableComponent) table: DatatableComponent;

  constructor(
    private _changeLanguageService: ChangeLanguageService,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService,
    public undoLogService: LogUndoService,
    private calendar: NgbCalendar,
    private router: Router
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      // console.log('lang' + this.currentLang == this.vn)
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
    this.contentHeader = {
      headerTitle: "CONTENT_HEADER.LOG_UNDO",
      actionButton: true,
      breadcrumb: {
        type: "",
        links: [
          {
            name: "CONTENT_HEADER.MAIN_PAGE",
            isLink: true,
            link: "/dashboard",
          },
          {
            name: "CONTENT_HEADER.ADMINISTRATOR",
            isLink: false,
            link: "/",
          },
          {
            name: "CONTENT_HEADER.LOG_UNDO",
            isLink: false,
          },
        ],
      },
    };
    this._coreTranslationService.translate(eng, vie);
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
    this.listAction = [
      { name: this._translateService.instant("LABEL_LOG.POST"), value: "POST" },
      { name: this._translateService.instant("LABEL_LOG.PUT"), value: "PUT" },
      {
        name: this._translateService.instant("LABEL_LOG.DELETE"),
        value: "DELETE",
      },
      { name: this._translateService.instant("LABEL_LOG.IMPORT"), value: "IMPORT" },
    ];
    this.search();
  }

  search() {
    let params = {
      method: "GET",
      tableName: this.tableName == null ? "" : this.tableName,
      action: this.action == null ? "" : this.action,
      startDate: this.valueFromDate ? this.valueFromDate : "",
      endDate: this.valueFromToDate ? this.valueFromToDate : "",
      createdBy: this.createdBy == null ? "" : this.createdBy,
      currentPage: this.currentPage,
      perPage: this.perPage,
    };
    Swal.showLoading();
    this.undoLogService
      .searchUndo(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
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
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  changeDate(value, type) {
    if (type == "startDate") {
      this.valueFromDate = this.convertDate(value);
    } else if (type == "endDate") {
      this.valueFromToDate = this.convertDate(value);
    }
    this.currentPage = 0;
    this.search();
  }

  convertDate(value: any): any {
    var date = value.year + "-" + value.month + "-" + value.day;
    return date;
  }

  clear() {
    this.currentPage = 0;
    this.search();
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.search();
  }

  changePerpage() {
    this.currentPage = 0;
    this.search();
  }

  undo(data) {
    //đưa thông báo
    Swal.fire({
      title: this._translateService.instant(
        "MESSAGE.AUTHENTICATION_MANAGEMENT.UNLOCK_CONFIRM"
      ),
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
          method: "POST",
          content: data,
        };
        //trả két quả luôn
        Swal.showLoading();
        this.undoLogService
          .undo(params)
          .then((data) => {
            Swal.close();
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant(
                  "MESSAGE.UNDO_LOG.UNDO_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                if (result.value) {
                  this.clear();
                }
              });
            } else if (response.code === 9) {
              this.showMessageError("MESSAGE.UNDO_LOG.NO_SUCCESS");
            } else if(response.code === 100){
              this.showMessageError("MESSAGE.UNDO_LOG.CATEGORY_BEING_USE");
            } else if(response.code === 101){
              this.showMessageError("MESSAGE.UNDO_LOG.EXIST_CATEGORY");
            } else if(response.code === 102){
              this.showMessageError("MESSAGE.UNDO_LOG.ORGANIZATION_BEING_USED");
            } else if(response.code === 103){
              this.showMessageError("MESSAGE.UNDO_LOG.ORGANIZATION_CATEGORY");
            } else if(response.code === 104){
              this.showMessageError("MESSAGE.UNDO_LOG.STANDARD_BEING_USED");
            } else if(response.code === 105){
              this.showMessageError("MESSAGE.UNDO_LOG.EXIST_STANDARD");
            } else if(response.code === 106){
              this.showMessageError("MESSAGE.UNDO_LOG.CRITERIA_BEING_USED");
            } else if(response.code === 107){
              this.showMessageError("MESSAGE.UNDO_LOG.EXIST_CRITERIA");
            } else if(response.code === 108){
              this.showMessageError("MESSAGE.UNDO_LOG.APP_PARAM_BEING_USED");
            } else if(response.code === 109){
              this.showMessageError("MESSAGE.UNDO_LOG.EXIST_APP_PARAM");
            }
            else if(response.code === 110){
              this.showMessageError("MESSAGE.UNDO_LOG.UNIT_1");
            }
            else if(response.code === 111){
              this.showMessageError("MESSAGE.UNDO_LOG.UNIT_2");
            } else if(response.code === 44){
                            this.showMessageError("MESSAGE.UNDO_LOG.EMAIL");

            }
            else {
              Swal.fire({
                icon: "error",
                title: response.errorMessages,
              });
            }
          })
          .catch((error) => {
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

  idDetail;
  objRevert;
  objRequest;
  openModal(obj, objRevert, modalDetail) {
    this.idDetail = obj.id;
    this.objRevert = objRevert ? JSON.parse(objRevert) : null;
    this.objRequest = JSON.parse(obj);
    this.modalService.open(modalDetail, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  showMessageError(key){
    Swal.fire({
      icon: "error",
      title: this._translateService.instant(
        key
      ),
      confirmButtonText:
        this._translateService.instant("ACTION.ACCEPT"),
    })
  }

  onChange1() {
  if( this.oker == null && this.oker0 == null){
      let params = {
        method: "GET",
        tableName: this.tableName == null ? "" : this.tableName,
        action: this.action == null ? "" : this.action,
        startDate: "",
        endDate:  "",
        createdBy: this.createdBy == null ? "" : this.createdBy,
        currentPage: this.currentPage,
        perPage: this.perPage,
      };
      Swal.showLoading();
      this.undoLogService
        .searchUndo(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.rows = response.content["items"];
            this.totalRows = response.content["total"];
          } else {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant(
                "MESSAGE.COMMON.CONNECT_FAIL"
              ),
            });
            if (response.code === 2) {
              this.rows = [];
              this.totalRows = 0;
            }
          }
        })
        .catch((error) => {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          });
        });
  }
  }


}
