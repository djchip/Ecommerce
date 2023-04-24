import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ColumnMode, DatatableComponent ,SelectionType} from '@swimlane/ngx-datatable';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from '../config.service';
import { CoreTranslationService } from '@core/services/translation.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-list-config',
  templateUrl: './list-config.component.html',
  styleUrls: ['./list-config.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListConfigComponent implements OnInit {

  public contentHeader: object;
  public documentTypeHeader: object;
  public listDocumentType = [];
  public listField = [];
  public listDateFormat = [];
  public currentLang = this._translateService.currentLang;
  public keywordD = "";
  public tempD = [];
  public rowsD = [];
  public tempDataD = this.rowsD;
  public ColumnMode = ColumnMode;
  public currentPageD = 0;
  public perPageD = 10;
  public totalRowsD = 0;

  public keywordF = "";
  public tempF = [];
  public rowsF = [];
  public tempDataF = this.rowsF;
  public currentPageF = 0;
  public perPageF = 10;
  public totalRowsF = 0;

  public keywordE = "";
  public tempE = [];
  public rowsE = [];
  public tempDataE = this.rowsF;
  public currentPageE = 0;
  public perPageE = 10;
  public totalRowsE = 0;
  public selected = [];
  public chkBoxSelected = [];
  public selected1 = [];
  public chkBoxSelected1 = [];
  public SelectionType = SelectionType;
  public SelectionType1 = SelectionType;
public deleted=true;
public deleted1=true;

public proForm: FormGroup;
  public saveDateFormatForm: FormGroup;
  public saveDateFormatFormSubmitted = false;
  public keywordDT = "";
  public tempDT = [];
  public rowsDT = [];
  public listDateTimeFormat = [];
  public tempDataDT = this.rowsF;
  public currentPageDT = 0;
  public perPageDT = 10;
  public totalRowsDT = 0;
  public appParamId = null;
  public dateFormatSelected;
  public messages;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private service: ConfigService,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService,
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
      this.searchDocumentType();
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRowsD;
      // document.getElementsByClassName("page-count")[1].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRowsF;
      // document.getElementsByClassName("page-count")[2].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRowsE;
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
    this.initForm();
    this.getDateFormatSelected();
    this.documentTypeHeader = {
      headerTitle: 'CONTENT_HEADER.CONFIG_OTHERS'
    };

    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.CONFIG_OTHERS',
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
            name: 'CONTENT_HEADER.CONFIG_OTHERS',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.searchDocumentType();
    this.searchField();
    this.searchExhCode();
    this.getListDateTimeFormat();
    this.proForm = this.formBuilder.group({
      app_param: ['']
    })
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  initForm() {
    this.saveDateFormatForm = this.formBuilder.group({
      format: [null, [Validators.required]],
    })
  }

  get SaveDateFormatForm() {
    return this.saveDateFormatForm.controls;
  }

  fillForm(){
    this.saveDateFormatForm.patchValue({
      format : this.dateFormatSelected,
    })
    console.log('selecetd' + this.dateFormatSelected);
  }

  getDateFormatSelected(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getDateFormatSelected(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          // this.dateFormatSelected = data.content;
          // this.fillForm();
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

  saveDateFormat() {
    this.saveDateFormatFormSubmitted = true;

    if (this.saveDateFormatForm.invalid) {
      return;
    }
    let content = this.saveDateFormatForm.value;
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .saveDateTimeFormat(params, this.appParamId)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('LABEL_APP_PARAM.SAVE_SUCCESS'),
          }).then((result) => {
            localStorage.removeItem("dateFormat");
            this.listDateTimeFormat.forEach(element => {
              if(element.id == this.appParamId){
                this.dateFormat = element.name
              }
            });
            localStorage.setItem("dateFormat", this.dateFormat)
            console.log(this.dateFormat)
            this.modalService.dismissAll();
          });
        } else {
          console.log("err");
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        console.log("catch");
        Swal.close();
        Swal.fire({
          icon: "error",
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });
  }

  // Document Type

  setPageD(pageInfo) {
    this.currentPageD = pageInfo.offset;
    this.searchDocumentType();
  }

  changePerpageD() {
    this.currentPageD = 0
    this.searchDocumentType();
  }

  searchDocumentType() {
    let params = {
      method: "GET",
      keyword: this.keywordD,
      currentPage: this.currentPageD,
      perPage: this.perPageD
    };
    Swal.showLoading();
    this.service
      .searchDocumentType(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          this.rowsD = response.content["items"];
          this.totalRowsD = response.content["total"];

        } else {
          if (response.code === 2) {
            this.rowsD = [];
            this.totalRowsD = 0;
          }
        }
        // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRowsD;
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

  editDocumentType(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteDocumentType(id: number) {
    Swal.fire({
      title: this._translateService.instant('LABEL_APP_PARAM.DELETE_CONFIRM'),
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
              title: this._translateService.instant('LABEL_APP_PARAM.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchDocumentType();
            });
          } else if (response.code === 12) {
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant("LABEL_APP_PARAM.CANNOT_DELETE"),
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

  idDetail
  ObjDetail
  detailDocumentType(Document, modalSM){
    this.idDetail=Document.id;
    this.ObjDetail=Document;

    this.modalService.open(modalSM, {
      centered : true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  openModalAddDocumentType(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  afterCreateDocumentType() {
    this.modalService.dismissAll();
    this.currentPageD = 0
    this.searchDocumentType();
  }

  afterEditDocumentType() {
    this.modalService.dismissAll();
    this.currentPageD = 0
    this.searchDocumentType();
  }


  // Field
  setPageF(pageInfo) {
    this.currentPageF = pageInfo.offset;
    this.searchField();
  }

  changePerpageF() {
    this.currentPageF = 0
    this.searchField();
  }

  searchField() {
    let params = {
      method: "GET",
      keyword: this.keywordF,
      currentPage: this.currentPageF,
      perPage: this.perPageF
    };
    Swal.showLoading();
    this.service
      .searchField(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          this.rowsF = response.content["items"];
          this.totalRowsF = response.content["total"];

        } else {
          if (response.code === 2) {
            this.rowsF = [];
            this.totalRowsF = 0;
          }
        }
        // document.getElementsByClassName("page-count")[1].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRowsF;
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

  openModalAddField(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  afterCreateField() {
    this.modalService.dismissAll();
    this.currentPageF = 0
    this.searchField();
  }

  afterEditField() {
    this.modalService.dismissAll();
    this.currentPageF = 0
    this.searchField();
  }

  editField(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteField(id: number) {
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


  //Exh code
  setPageE(pageInfo) {
    this.currentPageE = pageInfo.offset;
    this.searchExhCode();
  }

  changePerpageE() {
    this.currentPageE = 0
    this.searchExhCode();
  }

  searchExhCode() {
    let params = {
      method: "GET",
      keyword: this.keywordE,
      currentPage: this.currentPageE,
      perPage: this.perPageE
    };
    Swal.showLoading();
    this.service
      .searchExhCode(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          this.rowsE = response.content["items"];
          this.totalRowsE = response.content["total"];

        } else {
          if (response.code === 2) {
            this.rowsE = [];
            this.totalRowsE = 0;
          }
        }
        // document.getElementsByClassName("page-count")[2].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRowsE;
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

  openModalAddExhCode(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  afterCreateExhCode() {
    this.modalService.dismissAll();
    this.currentPageE = 0
    this.searchExhCode();
  }

  afterEditExhCode() {
    this.modalService.dismissAll();
    this.currentPageE = 0
    this.searchExhCode();
  }

  editExhCode(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteExhCode(id: number) {
    Swal.fire({
      title: this._translateService.instant('LABEL_APP_PARAM.DELETE_EXHCODE_CONFIRM'),
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
              title: this._translateService.instant('LABEL_APP_PARAM.DELETE_EXHCODE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchExhCode();
            });
          } else if (response.code === 12) {
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant("LABEL_APP_PARAM.CANNOT_EXHCODE_DELETE"),
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



  //Date format

  setPageDT(pageInfo) {
    this.currentPageD = pageInfo.offset;
    this.searchDocumentType();
  }

  changePerpageDT() {
    this.currentPageD = 0
    this.searchDocumentType();
  }

  getListDateTimeFormat() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListDateTimeFormat(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listDateTimeFormat = data.content;
          this.listDateTimeFormat.forEach(element => {
            if(element.selectedFormat == 1){
              this.appParamId = element.id
            }
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

  searchDateTimeFormat() {
    let params = {
      method: "GET",
      keyword: this.keywordDT,
      currentPage: this.currentPageDT,
      perPage: this.perPageDT
    };
    Swal.showLoading();
    this.service
      .searchDateTimeFormat(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          this.rowsDT = response.content["items"];
          this.totalRowsDT = response.content["total"];

        } else {
          if (response.code === 2) {
            this.rowsDT = [];
            this.totalRowsDT = 0;
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


  
  onDeleteMulti(){
    Swal.fire({
      title: this._translateService.instant('LABEL_APP_PARAM.NOT_DELETED'),
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
        dto.app_param = arr;
        let params = {
          method: 'DELETE', content: dto.app_param,
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('LABEL_APP_PARAM.NOT_DELETED1'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.deleted= true;
              this.deleted1= true;
              this.searchDocumentType();
              this.searchExhCode();
              this.searchField();
            });
          } else if (response.code === 12) {
            Swal.fire(this._translateService.instant('LABEL_APP_PARAM.NOT_DELETED2'));
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
  customChkboxOnSelect1({ selected }) {
    this.chkBoxSelected1.splice(0, this.chkBoxSelected1.length);
    this.chkBoxSelected1.push(...selected);
  }
  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if(this.selected.length > 0){
      this.deleted = false;
    } else {
      this.deleted = true;  
    }
  
  }

  onSelect1({ selected1 }) {
    this.selected1.splice(0, this.selected1.length);
    this.selected1.push(...selected1);
    if(this.selected1.length > 0){
      this.deleted1 = false;
    } else {
      this.deleted1 = true;  
    }
  
  }
}
