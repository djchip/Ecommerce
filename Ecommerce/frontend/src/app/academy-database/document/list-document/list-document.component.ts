import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { Router } from '@angular/router';
import { DocumentService } from './../document.service';
import { DatatableComponent, ColumnMode, SelectionType } from '@swimlane/ngx-datatable';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import Swal from 'sweetalert2';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-list-document',
  templateUrl: './list-document.component.html',
  styleUrls: ['./list-document.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListDocumentComponent implements OnInit {

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
  public listDocument;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public currentLang = this._translateService.currentLang;
  vn = "vn";
  en = "en";
  public selected = [];
  public deleted = true;
  public canDownload = true;
  public chkBoxSelected = [];
  public SelectionType = SelectionType;
  public messages;
  public listIdAndFileNameDocument = [];
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  public acceptActionProof: any;
  constructor(
    private service: DocumentService,
    private router: Router,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    private _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService) {
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
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
      if(element.url === "/exhibition/proof"){
        this.acceptActionProof = element.action;
      }
    });
    this.searchDocument();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_DOCUMENT',
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
            name: 'CONTENT_HEADER.DOCUMENT_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.messages = {
      emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
      totalMessage: this._translateService.instant('LABEL.TOTAL')
    };
  }
  
  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if (this.selected.length > 0) {
      this.deleted = false;
      this.canDownload = false;
    } else {
      this.deleted = true;
      this.canDownload = true;
    }

    this.selected.map((e) => {
      console.log(e);
    });
  }

  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }

  onDeleteMulti() {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.DOCUMENT_MANAGEMENT.DELETE_CONFIRM"),
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
                  "MESSAGE.DOCUMENT_MANAGEMENT.DELETE_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.deleted= true;
                this.searchDocument();
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
                  "MESSAGE.DOCUMENT_MANAGEMENT.CANNOT_DELETE"
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
  searchDocument() {
    let params = {
      method: "GET", keyword: this.keyword, currentPage: this.currentPage, perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchDocument(params)
      .then((data) => {
        Swal.close();
        let response: any = data;
        // console.log(data);

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
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });
  }
  idDetail
  objDetail
  detailDocument(document, modalSM) {
    this.idDetail = document.id;
    this.objDetail = document;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });
  }

  editDocument(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });
  }

  deleteDocument(id: number) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.DELETE_CONFIRM'),
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
        this.service.deleteDocument(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.DOCUMENT_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchDocument();
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

  arrSelected = "0";
  onExportToExcel() {
    let date = new Date();
    let fileName = "Document_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
    if (this.selected.length > 0) {
      this.arrSelected = "";
      this.selected.map((e) => {
        this.arrSelected += e["id"] + ",";
      });
    }

    if (this.currentLang == this.vn) {
      this.service.exportExcel(fileName, "excel", this.arrSelected);
    } else {
      this.service.exportExcel(fileName, "excel-en", this.arrSelected);
    }

  }
  
  listArr = "";
  downloadMultipleFile() {
    this.selected.forEach((e) => {
      this.listArr += e.id + ",";
    })
    let params = {
      method: 'GET',
      listArr: this.listArr
    }

    this.service.getIdFileDTO(params)
      .then((data) => {
        this.listIdAndFileNameDocument = data.content;
        this.listIdAndFileNameDocument.forEach((e) => {
          this.service.download(e.id, e.fileName)
        })
      })


    // this.selected.map(element => {
    //   this.service.getFilenameById(params, element.id).then((data) => {
    //     this.fileName = data.content;
    //   })
    //   this.service.download(element.id, this.fileName);
    // });
  }

  // onExportToExcelEn(){
  //   let date = new Date();
  //   let fileName = "Document_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx";
  //   this.service.export(fileName, "excelEn");
  // }

  onExportToPDF() {
    let date = new Date();
    let fileName = "Document_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".pdf";
    this.service.export(fileName, "pdf");
  }

  onExportToCsv() {
    let date = new Date();
    let fileName = "Document_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".csv";
    this.service.export(fileName, "csv");
  }

  afterCreateDocument() {
    this.modalService.dismissAll();
    this.searchDocument();
  }

  afterEditDocument() {
    this.modalService.dismissAll();
    this.searchDocument();
  }

  openModalAddDocument(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  copyDocument(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl'
    });
  }

  afterCreateProof() {
    this.modalService.dismissAll();
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchDocument();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchDocument();
  }
}
