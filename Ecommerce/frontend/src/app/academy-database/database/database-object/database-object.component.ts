import { FormService } from './../../form-management/form.service';
import { ChangeLanguageService } from './../../../services/change-language.service';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ColumnMode, DatatableComponent, SelectionType } from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import { log } from 'console';
import { Router } from '@angular/router';

@Component({
  selector: 'app-database-object',
  templateUrl: './database-object.component.html',
  styleUrls: ['./database-object.component.scss']
})
export class DatabaseObjectComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  emittedEvents($event) {
    console.log('Action : ', $event);
  }

  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public messages;
  public canDelete = true;
  public canDownloadDb = true;
  public canDownloadForm = true;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  public SelectionType = SelectionType;
  public selected = [];
  public chkBoxSelected = [];

  constructor(
    private service: FormService,
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
    this.messages = {
      emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
      totalMessage: this._translateService.instant('LABEL.TOTAL')
    };
  }

  name;
  yearOfApplication;
  uploadTime;
  searchForm() {
    let params = {
      method: "GET",
      name: this.name ? this.name.trim() : "",
      yearOfApplication: this.yearOfApplication ? this.yearOfApplication : "",
      uploadTime: this.uploadTime ? this.uploadTime : "",
      currentPage: this.currentPage,
      perPage: this.perPage,
    };
    Swal.showLoading();
    this.service
      .searchDatabase(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.rows = response.content["items"];
          this.totalRows = response.content["total"];

        } else {
          // Swal.fire({
          //   icon: "warning",
          //   title: this._translateService.instant("MESSAGE.FORM.FIND_NOT"),
          // });
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

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchForm();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchForm();
  }

  copyId
  openModalUpload(id, modalSM) {
    this.copyId = id;
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  afterUpload() {
    this.currentPage = 0;
    this.searchForm();
    this.modalService.dismissAll();
  }

  downloadForm(id, fileName) {
    console.log(id);
    console.log(fileName)
    this.service.download(id, fileName);
  }

  downloadMultipleForm() {
    this.selected.map((e) => {
      this.downloadForm(e.id, e.fileName);
    })
  }

  onDownload(id, fileName, isForm) {
    // var splitted = fileName.split("\\");
    var splitted = fileName.split("/");
    const fileName_ = splitted[splitted.length - 1];
    const link = document.createElement('a');
    link.setAttribute('target', '_blank');
    if (isForm) {
      link.setAttribute('href', 'assets/form-file/' + fileName_);
    } else {
      link.setAttribute('href', 'assets/form-file/csdl/' + fileName_);
    }
    link.setAttribute('download', fileName_);
    document.body.appendChild(link);
    link.click();
    link.remove();
  }

  // selectAll(event) {
  //   const isChecked = event.target.checked;
  //   if (isChecked) {
  //     this.listRecord = this.rows;
  //   }
  //   else {
  //     this.listRecord = [];
  //   }
  //   if(this.listRecord.length > 0) {
  //     this.canDelete = false;
  //     this.canDownloadDb = false;
  //     this.canDownloadForm = false;
  //   } else {
  //     this.canDelete = true;
  //     this.canDownloadDb = true;
  //     this.canDownloadForm = true;
  //   }
  // }

  // listRecord = []
  // onCheckboxChange(event, index) {
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
  //     this.canDelete = false;
  //     this.canDownloadDb = false;
  //     this.canDownloadForm = false;
  //   } else {
  //     this.canDelete = true;
  //     this.canDownloadDb = true;
  //     this.canDownloadForm = true;
  //   }
  // }

  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if (this.selected.length > 0) {
      this.canDelete = false;
      this.canDownloadDb = false;
      this.canDownloadForm = false;
    } else {
      this.canDelete = true;
      this.canDownloadDb = true;
      this.canDownloadForm = true;
    }
  }

  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }

  downloadDatabase(id, pathFileDatabase) {
    let arr = pathFileDatabase.split("/");
    this.service.downloadDB(id, arr[arr.length - 1])
  }

  downloadMultipleDatabase() {
    this.selected.map(e => {
      this.downloadDatabase(e.id, e.pathFileDatabase);
    })
  }

  deleteForm(row) {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.FORM.DEL_DB_CONFIRM"),
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
        let forms = [row];
        let params = {
          method: "DELETE",
          content: forms
        };
        this.service
          .deleteMultiCSDL(params)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant("MESSAGE.FORM.DEL_DB_SUCCESS"),
              }).then((result) => {
                //load lại trang kết quả
                this.canDelete = true;
                this.currentPage = 0;
                this.searchForm();
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
              title: "Không kết nối được tới hệ thống.",
              confirmButtonText: "OK",
            });
          });
      }
    });
  }

  deleteMultipleDatabase() {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.FORM.DEL_DB_CONFIRM"),
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
        this.service.deleteMultiCSDL(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.close();
            Swal.fire({
              icon: "success",
              title: this._translateService.instant("MESSAGE.FORM.DEL_DB_SUCCESS"),
              confirmButtonText: 'OK',
            }).then(data => {
              if (data.value) {
                this.canDelete = true;
                this.canDownloadDb = true;
                this.canDownloadForm = true;
                this.selected = [];
                this.currentPage = 0;
                this.searchForm();
              }
            })
          } else {
            Swal.close();
            Swal.fire({
              icon: "error",
              title: "Thực hiện xóa không thành công.",
            })
          }
        })
      }
    })

  }
}
