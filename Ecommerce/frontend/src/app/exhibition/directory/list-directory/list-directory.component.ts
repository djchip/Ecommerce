import { ChangeLanguageService } from 'app/services/change-language.service';
import { style } from '@angular/animations';
import { AlignmentType, Document, HeadingLevel, HeightRule, Packer, Paragraph, Table, TableCell, TableRow, TextDirection, UnderlineType, WidthType } from "docx";
import { Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import { DirectoryService } from "../directory.service";
import Swal from "sweetalert2";
import { ColumnMode, DatatableComponent, SelectionType } from "@swimlane/ngx-datatable";
import { Router } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { formatDate } from "@angular/common";

import { TranslateService } from "@ngx-translate/core";
import { CoreTranslationService } from "@core/services/translation.service";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import * as fs from "file-saver";
import { Workbook } from "exceljs";
import { TokenStorage } from 'app/services/token-storage.service';
import { ProgramService } from 'app/exhibition/programs-management/programs.service';

@Component({
  selector: "app-list-directory",
  templateUrl: "./list-directory.html",
  styleUrls: ["./list-directory.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ListDirectoryComponent implements OnInit {
  @ViewChild(DatatableComponent) table: DatatableComponent;

  public listMenu;
  public selected = [];
  public listStaId = [];
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
  public idPro = "";
  public idOrg = "";
  public listPrograms = [];
  public listOrg = [];
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public roleAdmin = window.localStorage.getItem("ADM");
  public currentUserName;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  public disablePro = true;

  constructor(
    private service: DirectoryService,
    private router: Router,
    private tokenStorage: TokenStorage,
    private modalService: NgbModal,
    public _translateService: TranslateService,
    private _coreTranslationService: CoreTranslationService,
    private _changeLanguageService: ChangeLanguageService,
    private programService: ProgramService,
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
      this.messages = {
        emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
        totalMessage: this._translateService.instant('LABEL.TOTAL')
      };
      this.searchDirectory();
    })
  }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    this.currentUserName = this.tokenStorage.getUsername();
    this.getListStandardIdByUsername();
    console.log(this.listStaId + ' chien')
    this._coreTranslationService.translate(eng, vie);
    this.searchDirectory();
    this.getListOrg();

    this.contentHeader = {
      headerTitle: "CONTENT_HEADER.LIST_DIRECTORY",
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
            name: "CONTENT_HEADER.EXHIBITION",
            isLink: false,
            link: "/",
          },
          {
            name: "CONTENT_HEADER.LIST_DIRECTORY",
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

  getListOrg() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListOrganization(params)
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

  onDeleteMulti() {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.DIRECTORY.DELETE_CONFIRM'),
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
        this.selected.map(e => { arr.push(e['id']) })
        if (this.roleAdmin == 'false') {
          arr.forEach(element => {
            if (this.listStaId.includes(element)) {
              array.push(element)
            }
          })
        } else {
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
              title: this._translateService.instant('MESSAGE.DIRECTORY.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              this.deleted= true;
              this.searchDirectory();
            });
          } else if (response.code === 12) {
            Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.BEING_USED_STANDARD'));
          } else if (response.code === 30) {
            Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.NO_RECORD_DELETED'));
          } else {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.DIRECTORY.CANNOT_DELETE'),
            });
          }
        }).catch((error) => {
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

  searchDirectory() {
    if (this.orgId == null) {
      this.idOrg = "";
    } else {
      this.idOrg = this.orgId;
    }
    if (this.programId == null) {
      this.idPro = "";
    } else {
      this.idPro = this.programId;
    }
    let params = {
      method: "GET",
      keyword: this.keyword,
      lang: this._translateService.currentLang,
      orgId: this.idOrg,
      programId: this.idPro,
      currentPage: this.currentPage,
      perPage: this.perPage,
      isExcel: false
    };
    this.getListStandardIdByUsername();
    Swal.showLoading();
    this.service
      .searchDirectory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          //update time chuẩn dạng ngày/tháng/năm
          // for(let i = 0; i< data.content["items"].length ; i++){
          //   let a = data.content["items"][i]["createdDate"];
          //   let b = data.content["items"][i]["updatedDate"];
          //   if(data.content["items"][i]["createdDate"] != null){
          //     data.content["items"][i]["createdDate"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0];
          //   }
          //   if(data.content["items"][i]["updatedDate"] != null && data.content["items"][i]["update_by"] != null){
          //     data.content["items"][i]["updatedDate"] = b.split("T")[0].split("-")[2] + "-" + b.split("T")[0].split("-")[1] + "-" + b.split("T")[0].split("-")[0];
          //   } else {
          //     data.content["items"][i]["updatedDate"] = "";
          //   }
          // }
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
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: "OK",
        });
      });
  }

  onChange() {
    this.searchDirectory();
  }

  onChangeOrg() {
    this.programId = null;
    this.disablePro = false;
    this.getListPrograms();
    this.searchDirectory();
  }

  resetProgram() {
    // this.programId = this.idPro;
    this.disablePro = true;
    this.searchDirectory();
  }

  getListPrograms() {
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
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  directories
  listIdSelected = "0";
  getAllDirectory() {
    if (this.selected.length > 0) {
      this.selected.forEach((e) => {
        this.listIdSelected += e.id + ',';
      })
    }
    console.log(this.listIdSelected)

    let params = {
      method: "GET",
      keyword: this.keyword,
      lang: this._translateService.currentLang,
      programId: this.idPro,
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
          this.directories = response.content["items"];
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
      .then(e => {
        this.downloadExcelFile();
      });
  }

  addForm() {
    this.router.navigate(["/admin/directory/add-directory"]);
  }

  idDetail;
  ObjDetail;
  detailDirectory(directory, modalSM) {
    this.idDetail = directory.id;
    this.ObjDetail = directory;
    // window.localStorage.removeItem("id");
    // window.localStorage.setItem("id", id);
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

  editId
  editDirectory(id, modalSM) {
    // window.localStorage.removeItem("id");
    // window.localStorage.setItem("id", id);
    this.editId = id;
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  getListStandardIdByUsername() {
    let params = { method: "GET" }
    this.service
      .getListStandardIdByUsername(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listStaId = response.content;
        } else {
          this.listStaId = [];
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

  deletelDirector(id) {
    Swal.fire({
      title: this._translateService.instant("MESSAGE.DIRECTORY.DELETE_CONFIRM"),
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
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
          .deletDirectory(params, id)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant(
                  "MESSAGE.DIRECTORY.DELETE_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.searchDirectory();
              });
            } else if (response.code === 12) {
              Swal.fire({
                icon: "warning",
                title: this._translateService.instant(
                  "MESSAGE.DIRECTORY.CANNOT_DELETE"
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
          })
          ;
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
    this.currentPage = 0;
    this.searchDirectory();
  }

  afterEditDirectory() {
    this.modalService.dismissAll();
    this.currentPage = 0;
    this.getListStandardIdByUsername();
    this.searchDirectory();
  }

  onExportToExcel() {
    let date = new Date();
    let fileName =
      "Directory_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", "en-US") + ".xlsx";
    this.service.exportDirectoryExcel(fileName);
  }

  onExportToPDF() {
    let date = new Date();
    let fileName =
      "Directory_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", "en-US") + ".pdf";
    this.service.exportDirectoryPDF(fileName);
  }


  downloadExcelFile() {
    let objs = this.directories;

    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet("Sheet1");

    let headerRow = worksheet.addRow(
      [
        this._translateService.instant("LABEL.NO"),
        this._translateService.instant("LABEL.NAME"),
        this._translateService.instant("LABEL.CODE"),
        this._translateService.instant("LABEL.PROGRAMS"),
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
        d.programName,
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
      fs.saveAs(blob, "Driectory_" + formatDate(date, "dd-MM-yyyy_hh-mm-ss", 'en-US') + ".xlsx");
    });
  }

}
