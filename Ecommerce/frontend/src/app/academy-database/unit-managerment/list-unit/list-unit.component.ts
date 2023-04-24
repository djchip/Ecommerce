import { ChangeLanguageService } from "app/services/change-language.service";
import {
  Component,
  OnInit,
  ViewChild,
  ViewEncapsulation,
  ElementRef,
} from "@angular/core";
import { ColumnMode, DatatableComponent, SelectionType } from "@swimlane/ngx-datatable";
import Swal from "sweetalert2";
import { Router } from "@angular/router";
import { UnitManagementService } from "../unit-managerment.service";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { TranslateService } from "@ngx-translate/core";
import { CoreTranslationService } from "@core/services/translation.service";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import { ExcelService } from "../../../services/excel.service";
import { HttpClient } from "@angular/common/http";
import { HttpResponse, HttpEventType } from "@angular/common/http";
import { formatDate } from "@angular/common";

@Component({
  selector: "app-list-unit",
  templateUrl: "./list-unit.component.html",
  styleUrls: ["./list-unit.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ListUnitComponent implements OnInit {
  title = "File-Upload-Save";
  selectedFiles: FileList;
  currentFileUpload: File;
  fileName: string;
  progress: { percentage: number } = { percentage: 0 };
  selectedFile = null;
  changeImage = false;

  @ViewChild(DatatableComponent) table: DatatableComponent;
  @ViewChild("fileInput", { static: false })
  myFileInput: ElementRef;

  public keyword = "";
  public classify = null;
  public classifyBE = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public contentHeader: object;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  // public listClassify = [{id: 1, name: "Văn phòng học viện", nameEn: "Academy office"},
  //                       {id: 2, name: "Khoa", nameEn: "Faculty"},
  //                       {id: 3, name: "Viện", nameEn: "Institute"},
  //                       {id: 4, name: "Trung tâm", nameEn: "Center"},
  //                       {id: 5, name: "Công ty", nameEn: "Company"}];
  public listClassify = [
    { id: 1, name: "Ban chức năng", nameEn: "Committee Functional" },
    { id: 2, name: "Khoa", nameEn: "Faculty" },
    {
      id: 3,
      name: "Viện, Trung Tâm, Công Ty",
      nameEn: "Institute, Center, Company",
    },
  ];
  public selected = [];
  public deleted = true;
  public chkBoxSelected = [];
  public SelectionType = SelectionType;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  constructor(
    private router: Router,
    private service: UnitManagementService,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService,
    private excelService: ExcelService,
    private _changeLanguageService: ChangeLanguageService
  ) {
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
      headerTitle: "CONTENT_HEADER.LIST_UNIT",
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
            name: "MENU.DATABASE",
            isLink: false,
            link: "/",
          },
          {
            name: "CONTENT_HEADER.UNIT_MANAGERMENT",
            isLink: false,
          },
        ],
      },
    };
    this._coreTranslationService.translate(eng, vie);
    this.searchUnit();
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  onChange() {
    this.searchUnit();
  }

  addUnit() {
    this.router.navigate(["/admin/unit/add-unit"]);
  }

  importUnit() {
    this.router.navigate(["/admin/unit/import-unit"]);
  }

  editUnit(unitId, modalSM) {
    window.localStorage.removeItem("unitId");
    window.localStorage.setItem("unitId", unitId);
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
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
      title: this._translateService.instant("MESSAGE.UNIT_MANAGERMENT.DELETE_CONFIRM"),
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
                  "MESSAGE.UNIT_MANAGERMENT.DELETE_SUCCESS"
                ),

                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.deleted= true;
                this.searchUnit();
              });
            } else if (response.code === 12) {
              Swal.fire(
                this._translateService.instant(
                  "MESSAGE.PROGRAM_MANAGEMENT.NOT_DELETED"
                )
              );
            } else {
              Swal.fire({
                icon: "error",
                title: this._translateService.instant(
                  "MESSAGE.UNIT_MANAGERMENT.CANNOT_DELETE"
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
  searchUnit() {
    if (this.classify == null) {
      this.classifyBE = "";
    } else {
      this.classifyBE = this.classify;
    }
    let params = {
      method: "GET",
      keyword: this.keyword,
      classify: this.classifyBE,
      currentPage: this.currentPage,
      perPage: this.perPage,
    };
    Swal.showLoading();
    this.service
      .searchUnit(params)
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
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }
  idDetail;
  objDetail;
  detailUnit(unit, modalSM) {
    this.idDetail = unit.id;
    this.objDetail = unit;
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteUnit(id: number) {
    Swal.fire({
      title: this._translateService.instant(
        "MESSAGE.UNIT_MANAGERMENT.DELETE_CONFIRM"
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
          method: "DELETE",
        };
        this.service
          .deleteUnit(params, id)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant(
                  "MESSAGE.UNIT_MANAGERMENT.DELETE_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.searchUnit();
              });
            } else if (response.code === 12) {
              Swal.fire({
                icon: "warning",
                title: this._translateService.instant(
                  "MESSAGE.UNIT_MANAGERMENT.CANNOT_DELETE"
                ),
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

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchUnit();
  }

  changePerpage() {
    this.currentPage = 0;
    this.searchUnit();
  }

  // modal Open Small
  openModalAddUnit(modalSLCIM) {
    this.modalService.open(modalSLCIM, {
      scrollable: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  openModalImportUnit(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterCreateUnit() {
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchUnit();
  }
  afterImportUnit() {
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchUnit();
  }

  afterEditUnit() {
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchUnit();
  }

  exportToExcel() {
    let date = new Date();
    let filename =
      "Unit_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", "en-US") + ".xlsx";
    if (this.currentLang == "vn") {
      this.service.export(filename);
    } else {
      this.service.exportEn(filename);
    }
  }
  reset() {
    this.myFileInput.nativeElement.value = "";
  }

  change($event) {
    this.changeImage = true;
  }

  changedImage(event) {
    this.selectedFile = event.target.files[0];
  }

  upload() {
    this.progress.percentage = 0;

    this.currentFileUpload = this.selectedFiles.item(0);
    this.service
      .pushFileToStorage(this.currentFileUpload)
      .subscribe((event) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.progress.percentage = Math.round(
            (100 * event.loaded) / event.total
          );
        } else if (event instanceof HttpResponse) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant(
              "MESSAGE.UNIT_MANAGERMENT.IMPORT_SUCCESS"
            ),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          }).then((result) => {
            //load lại trang kết quả
            this.searchUnit();
            this.reset();
          });
        }
        this.selectedFiles = undefined;
      });
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }
}
