import { ChangeLanguageService } from 'app/services/change-language.service';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { CriteriaService } from '../criteria.service';
import Swal from "sweetalert2";
import { ColumnMode, DatatableComponent, SelectionType } from '@swimlane/ngx-datatable';
import { Router } from "@angular/router";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { formatDate } from '@angular/common';

import { TranslateService } from '@ngx-translate/core';
import { HttpResponse, HttpEventType } from '@angular/common/http';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { Workbook } from "exceljs";
import * as fs from "file-saver";
import { ProgramService } from 'app/exhibition/programs-management/programs.service';
@Component({
  selector: 'app-list-criteria',
  templateUrl: './list-criteria.html',
  styleUrls: ['./list-criteria.scss'],
  encapsulation: ViewEncapsulation.None

})
export class ListCriteriaComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;
  public listMenu;
  public selected = [];
  public deleted = true;
  public chkBoxSelected = [];
  public SelectionType = SelectionType;
  public contentHeader: object;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public keyword = "";
  public programId = null;
  public orgId = null;
  public standardId = null;
  public idPro = "";
  public idOrg = "";
  public idSta = "";
  public id = "";
  disableSta = true;
  public listPrograms = [];
  public listStandard = [];
  public listOrg = [];
  public listCriId = [];
  public currentLang = this._translateService.currentLang;
  public roleAdmin = window.localStorage.getItem("ADM");
  public dateFormat = window.localStorage.getItem("dateFormat");
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  public disablePro = true;

  constructor(private service: CriteriaService, 
    private router: Router, 
    private modalService: NgbModal, 
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService, 
    private _changeLanguageService: ChangeLanguageService,
    private programService: ProgramService,
    ) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
        // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
        this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
        // this.searchDirectory();

      })
    }

  
  ngOnInit(): void {
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    this._coreTranslationService.translate(eng, vie);
    this.getListCriteriaIdByUsername();
    this.searchDirectory();
    // this.getListPrograms();
    this.getListStandard();
    this.getListOrg();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_CRITERIA',
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
            name: 'CONTENT_HEADER.EXHIBITION',
            isLink: false,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.LIST_CRITERIA',
            isLink: false
          }
        ]
      }
    };
    // console.log("oke"+this.listCriId)
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  getListOrg(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListOrganizationForCriteria(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrg = data.content;
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

  defaultRecord(row) {
    this.listCriId.forEach(ele => {
      if(row.id == ele){
        console.log(" false")
        return false;
      }
    })
    console.log(" true")
    return true;
    // if (row.id === "excadmin") {
    //   this.buttonDisabled = true;
    //   return true;
    // } else {
    //   this.buttonDisabled = false;
    //   return false;
    // }
  }

  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    // this.selected = selected.filter(e => this.defaultRecord(e));
    if(this.selected.length > 0){
      this.deleted = false;
    } else {
      this.deleted = true;
    }
  }

  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    // this.selected = selected.filter(e => this.defaultRecord(e));
    this.chkBoxSelected.push(...selected);
  }

  onDeleteMulti(){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.CRITERIA.DELETE_CONFIRM'),
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
        let array = [];
        this.selected.map(e => {arr.push(e['id'])})
        if(this.roleAdmin == 'false'){
          arr.forEach(element => {
            if(this.listCriId.includes(element)){
              array.push(element)
            }
          })
        }else{
          array = arr;
        }
        
        let params = {
          method: 'DELETE', content: array,
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.CRITERIA.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.deleted= true;
              this.searchDirectory();
            });
          } else if (response.code === 12) {
            Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.BEING_USED_CRITERIA'));
          } else if (response.code === 30) {
            Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.NO_RECORD_DELETED'));
          } else {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.CRITERIA.CANNOT_DELETE'),
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

  downloadExcelFile() {
    let objs = this.criteria;
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet("Sheet1");
    let headerRow = worksheet.addRow(
      [
        this._translateService.instant("LABEL.NO"),
        this._translateService.instant("LABEL_CRITERIA.NAME"),
        this._translateService.instant("LABEL_CRITERIA.CODE"),
        this._translateService.instant("LABEL.ORG"),
        this._translateService.instant("LABEL.STANDARD"),
        this._translateService.instant("LABEL.DES"),
        this._translateService.instant("LABEL.CREATE_DATE"),
        this._translateService.instant("LABEL.CREATE_BY"),
        this._translateService.instant("LABEL.UPDATE_DATE"),
        this._translateService.instant("LABEL.UPDATE_BY"),
      ]
    );

    // style for header
    headerRow.eachCell((cell, number) => {
      cell.font = {
        name: "Times New Roman",
        bold: true,
      };
      cell.border = {
        top: { style: "thin" },
        left: { style: "thin" },
        bottom: { style: "thin" },
        right: { style: "thin" },
      };
    });

    // insert data
    objs.forEach((d, i) => {
      let row = worksheet.addRow([
        i + 1,
        d.name,
        d.code,
        d.orgramName,
        d.standarName,
        d.description,
        d.createdDate == null ? "" : formatDate(d.createdDate, this.dateFormat, 'en-US'),
        d.create_by,
        d.updatedDate == null ? "" : formatDate(d.updatedDate, this.dateFormat, 'en-US'),
        d.update_by,
      ]);
      row.eachCell((cell) => {
        cell.font = {
          name: "Times New Roman",
        };
      });
    });

    // auto fit width
    worksheet.columns.forEach((column) => {
      const lengths = column.values.map((v) => v.toString().length);
      const maxLength = Math.max(
        ...lengths.filter((v) => typeof v === "number")
      );
      column.width = maxLength + 10;
      column.alignment = {
        vertical: "middle",
        horizontal: "center",
        wrapText: true,
      };
    });

    workbook.xlsx.writeBuffer().then((data) => {
      let date = new Date();
      let blob = new Blob([data], {
        type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      });
      fs.saveAs(blob, "Criteria_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx");
    });
  }

  searchDirectory() {
    if(this.orgId == null){
      this.idOrg = "";
    } else {
      this.idOrg = this.orgId;
    }
    if(this.programId == null){
      this.idPro = "";
    } else {
      this.idPro = this.programId;
    }
    if(this.standardId == null){
      this.idSta = "";
    } else {
      this.idSta = this.standardId;
    }
    this.getListCriteriaIdByUsername();
    let params = {
      method: "GET", 
      keyword: this.keyword, 
      lang: this._translateService.currentLang,
      orgId: this.idOrg, 
      programId: this.idPro, 
      standardId: this.idSta, 
      currentPage: this.currentPage, 
      perPage: this.perPage,
      isExcel: false
    };
    Swal.showLoading();
    this.service
      .searchDirectory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listMenu = response.content["items"];
          this.totalRows = response.content["total"];

        } else {
          if (response.code === 2) {
            this.listMenu = [];
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
          confirmButtonText: "OK",
        });
      });
  }
 
  addForm() {
    this.router.navigate(["/admin/directory/add-directory"]);
  }

  onChange(){
    this.searchDirectory();
  }

  resetProgram(){
    // this.standardId = this.idPro;
    this.standardId = null;
    this.disableSta = true;
  }

  onChangePro(){
    this.standardId = null;
    this.disableSta = false;
    this.searchDirectory();
  }

  resetPro(){
  }

  onChangeOrg(){
    this.getListStandard();
    this.standardId = null;
    this.programId = null;
    this.disablePro = false;
    this.searchDirectory();
    this.getListPrograms();
  }

  resetOrg(){
    this.programId = null;
    this.standardId = null;
    this.searchDirectory();
    this.disableSta = true;
    this.disablePro = true;
  }

  getListCriteriaIdByUsername(){
    let params = {method: "GET"}
    this.service
      .getListCriteriaIdByUsername(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listCriId = response.content;
        } else {
          this.listCriId = [];
        }
      })
      .catch((error) => {
        Swal.close();
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      })
  }

  getListPrograms(){
    if(this.orgId == null){
      return;
    }
    let params = {
      method: "GET",
      orgId: this.orgId
    };
    Swal.showLoading();
    this.programService
      .findByOrgId(params)
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

  getListStandard(){
    if(this.orgId == null){
      this.id = "";
    } else {
      this.id = this.orgId;
    }
    let params = {
      method: "GET", orgId: this.id,
    };
    Swal.showLoading();
    this.service
      .searchStandard(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listStandard = data.content;
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
  idDetail
  ObjDetail
  detailDirectory(criteria, modalSM) {
    this.idDetail=criteria.id;
    this.ObjDetail=criteria;
    // window.localStorage.removeItem("id");
    // window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
 
  // modal Open Small
  openModalAddMenus(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  openModalImport(modal) {
    this.modalService.open(modal, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editDirectory(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deletelDirector(id) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.CRITERIA.DELETE_CONFIRM'),
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
        this.service.deletDirectory(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.CRITERIA.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchDirectory();
            });
          } else if(response.code === 12){
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant('MESSAGE.CRITERIA.CANNOT_DELETE'),
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
    this.searchDirectory();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchDirectory();
  }


  afterCreateDirectory() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchDirectory();
  }

  afterEditDirectory() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchDirectory();
  }

  criteria
  listIdSelected = "0";
  getAllCriteria() {
    if (this.selected.length > 0) {
      this.selected.forEach((e) => {
        this.listIdSelected += e.id + ',';
      })
    }
    let params = {
      method: "GET", 
      keyword: this.keyword, 
      lang: this._translateService.currentLang,
      orgId: this.idOrg, 
      programId: this.idPro, 
      standardId: this.idSta, 
      currentPage: this.currentPage, 
      perPage: this.perPage,
      isExcel: true,
      listId: this.listIdSelected
    };
    Swal.showLoading();
    this.service
      .searchDirectoryExcel(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.criteria = response.content["items"];
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
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      })
      .then(e =>{
        this.downloadExcelFile();
      });
  }

}
