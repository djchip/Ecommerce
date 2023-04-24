import { ChangeLanguageService } from "./../../../services/change-language.service";
import { Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import { FormService } from "../form.service";
import Swal from "sweetalert2";
import { ColumnMode, DatatableComponent, SelectionType } from "@swimlane/ngx-datatable";
import { Router } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { formatDate } from "@angular/common";

import { TranslateService } from "@ngx-translate/core";
import { HttpResponse, HttpEventType } from "@angular/common/http";
import { CoreTranslationService } from "@core/services/translation.service";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import { StatisticalService } from "app/academy-database/statistical/statistical.service";

@Component({
  selector: "app-list-form",
  templateUrl: "./list-form.html",
  styleUrls: ["./list-form.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ListFormComponent implements OnInit {
  selectedFiles: FileList;
  currentFileUpload: File;
  fileName: string;
  progress: { percentage: number } = { percentage: 0 };
  selectedFile = null;
  changeImage = false;

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public rows;
  public contentHeader: object;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public messages;
  public canDetete = true;
  public canCopy = true;
  public canDownload = true;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  public selected = [];
  public chkBoxSelected = [];
  public SelectionType = SelectionType;

  constructor(
    private service: FormService,
    private statisticalService: StatisticalService,
    private router: Router,
    private modalService: NgbModal,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService,
    private _changeLanguageService: ChangeLanguageService
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
      this.messages = {
        emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
        totalMessage: this._translateService.instant('LABEL.TOTAL')
      };
    });
  }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if (this.router.url === element.url) {
        this.acceptAction = element.action;
      }
    });
    this._coreTranslationService.translate(eng, vie);
    this.searchForm();
    this.contentHeader = {
      headerTitle: "CONTENT_HEADER.LIST_FORM",
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
            name: "CONTENT_HEADER.FORM_MANAGEMENT",
            isLink: false,
          },
        ],
      },
    };
    this.messages = {
      emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
      totalMessage: this._translateService.instant('LABEL.TOTAL')
    };
  }

  onDownload(id, fileName) {
    Swal.showLoading();
    // var splitted = fileName.split("\\");
    var splitted = fileName.split("/");
    const fileName_ = splitted[splitted.length - 1];
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    link.setAttribute('href', 'assets/form-file/' + fileName_);
    link.setAttribute('download', fileName_);
    document.body.appendChild(link);
    link.click();
    link.remove();
    Swal.close();
  }

  downloadForm(id, fileName) {
    this.service.download(id, fileName);
  }

  name;
  yearOfApplication;
  uploadTime;
  searchForm() {
    let params = {
      method: "GET",
      name: this.name ? this.name : "",
      yearOfApplication: this.yearOfApplication ? this.yearOfApplication : "",
      uploadTime: this.uploadTime ? this.uploadTime : "",
      currentPage: this.currentPage,
      perPage: this.perPage,
    };
    Swal.showLoading();
    this.service
      .searchForm(params)
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
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });
  }

  addForm() {
    this.router.navigate(["/admin/from/add-form"]);
  }

  idDetail;
  objDetail;
  detailForm(form, modalSM) {
    this.idDetail = form.id;
    this.objDetail = form;
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  // modal Open Small
  openModalAddMenus(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  openModalMultiForm(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: "xl", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  copyForm(form_id, modalSM) {
    window.localStorage.removeItem("form_id");
    window.localStorage.setItem("form_id", form_id);
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg",
    });
  }

  afterCreateProof() {
    this.currentPage = 0;
    this.searchForm();
    this.modalService.dismissAll();
  }

  afterCopyForm() {
    this.canDownload = true;
    this.canDetete = true;
    this.canCopy = true;
    this.currentPage = 0;
    this.searchForm();
    this.modalService.dismissAll();
  }

  editForm(form_id, modalSM) {
    window.localStorage.removeItem("form_id");
    window.localStorage.setItem("form_id", form_id);
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteForm(row) {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.FORM.DELETE_CONFIM"),
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
        Swal.showLoading();
        let forms = [row];
        let params = {
          method: "DELETE",
          content: forms
        };
        this.service
          .deleteMulti(params)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant("MESSAGE.FORM.DELETE_SUCCESS"),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.currentPage = 0;
                this.searchForm();
              });
              // quannv
              let params = {
                method: "DELETE",
              };
              this.statisticalService.doRetrieveByForm(row.id, params);

            } else if (response.code === 12) {
              Swal.fire({
                title: this._translateService.instant("MESSAGE.FORM.CANNOT_DELETE"),
                // text: 'Xóa form cha sẽ xóa luôn các form con',
                icon: "warning",
                confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
                customClass: {
                  confirmButton: "btn btn-primary",
                },
              })
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
    this.searchForm();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchForm();
  }

  afterCreateForm() {
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchForm();
  }

  afterEditMenu() {
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.searchForm();
  }

  onExportToExcel() {
    let date = new Date();
    let filename =
      "Form_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", "en-US") + ".xlsx";
    this.service.exportForm(filename);
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
            this.searchForm();
            // this.reset();
          });
        }
        this.selectedFiles = undefined;
      });
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

  // listRecord = []
  // onCheckboxChange(event, index) {
  //   console.log("SELECTED");
  //   const isChecked = event.target.checked;
  //   if (isChecked) {
  //     this.rows[index].isChecked = true;
  //     //thêm element đã chọn vào list cần xóa
  //     this.listRecord.push(this.rows[index]);
  //   }
  //   else {
  //     this.rows[index].isChecked = false;
  //     // Xóa element đã bỏ chọn khỏi list cần xóa
  //     let listLength = this.listRecord.length;
  //     let elementRemove = this.rows[index];
  //     for (let i = 0; i < listLength; i++) {
  //       if (this.listRecord[i].id == elementRemove.id) {
  //         this.listRecord.splice(i, 1);
  //         break;
  //       }
  //     }
  //   };
  //   if(this.listRecord.length > 0) {
  //     this.canDetete = false;
  //     this.canCopy = false;
  //     this.canDownload = false;
  //   } else {
  //     this.canDetete = true;
  //     this.canCopy = true;
  //     this.canDownload = true;
  //   }
  // }

  // selectAll(event) {
  //   const isChecked = event.target.checked;
  //   if (isChecked) {
  //     this.listRecord = this.rows;
  //   }
  //   else {
  //     this.listRecord = [];
  //   }
  //   console.log(this.listRecord);
  //   if(this.listRecord.length > 0) {
  //     this.canDetete = false;
  //     this.canCopy = false;
  //     this.canDownload = false;
  //   } else {
  //     this.canDetete = true;
  //     this.canCopy = true;
  //     this.canDownload = true;
  //   }
  // }

  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if (this.selected.length > 0) {
      this.canDownload = false;
      this.canDetete = false;
      this.canCopy = false;
    } else {
      this.canDownload = true;
      this.canDetete = true;
      this.canCopy = true;
    }
  }

  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }

  deleteMulti() {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.FORM.DELETE_CONFIM"),
      // text: 'Xóa form cha sẽ xóa luôn các form con',
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
        Swal.showLoading();
        let forms = this.selected;
        let params = {
          method: 'DELETE',
          content: forms
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.close();
            Swal.fire({
              icon: "success",
              title: this._translateService.instant("MESSAGE.FORM.DELETE_SUCCESS"),
              confirmButtonText: 'OK',
            }).then(data => {
              if (data.value) {
                this.currentPage = 0;
                this.canDetete = true;
                this.canDownload = true;
                this.canCopy = true;
                this.searchForm();
              }
            })
          } else if (response.code === 12) {
            Swal.fire({
              title: this._translateService.instant("MESSAGE.FORM.CANNOT_DELETE"),
              // text: 'Xóa form cha sẽ xóa luôn các form con',
              icon: "warning",
              confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
              customClass: {
                confirmButton: "btn btn-primary",
              },
            })
          }
          else {
            Swal.close();
            Swal.fire({
              icon: "error",
              title: this._translateService.instant("MESSAGE.FORM.DELETE_FAILED"),
            })
          }
        })
      }
    })

  }

  downloadForms() {
    this.selected.map(e => {
      this.service.download(e.id, e.fileName);
      // this.onDownload(e.id, e.pathFile);
    })
  }
}
