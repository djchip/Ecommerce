import { TokenStorage } from "./../../../services/token-storage.service";
import { ChangeLanguageService } from "app/services/change-language.service";
import { TranslateService } from "@ngx-translate/core";
import { CoreTranslationService } from "@core/services/translation.service";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { AssessmentService } from "./../assessment.service";
import { DatatableComponent, ColumnMode, SelectionType } from "@swimlane/ngx-datatable";
import { Component, OnInit, ViewChild, ViewEncapsulation } from "@angular/core";
import Swal from "sweetalert2";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";
import { Router } from "@angular/router";

@Component({
  selector: "app-list-assessment",
  templateUrl: "./list-assessment.component.html",
  styleUrls: ["./list-assessment.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class ListAssessmentComponent implements OnInit {
  @ViewChild(DatatableComponent) table: DatatableComponent;

  public contentHeader: object;
  public keyword = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public reportType = 0;
  public perPage = 10;
  public totalRows = 0;
  public standardId = null;

  public idAss;
  public disableSta = true;
  public disableCrite = true;

  public programId = null;
  public directoryId = null;
  public idPro = "";
  public idDir = "";
  public listAss = [];
  public listStandard = [];
  public listPrograms = [];
  public listCrite = [];
  public criteriaId = null;
  public idCrite = "";
  public ar = [];
  public currentUserName;
  public listEvaluate = [
    { id: "", name: "Chưa đánh giá", nameEn: "Not yet rated" },
    { id: 0, name: "Chưa đánh giá", nameEn: "Not yet rated" },
    { id: 1, name: "Đã comment", nameEn: "Commented" },
    { id: 2, name: "Đã sửa", nameEn: "Fixed" },
    { id: 3, name: "Đạt", nameEn: "Achieve" },
    { id: 4, name: "Không đạt", nameEn: "Not achieved" },
  ];
  // public url = "https://10.252.10.236:9980/browser/a4b9c74/cool.html?WOPISrc=http://10.252.10.236:3232/neo/assessment/wopi/files/";
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public roleAdmin = window.localStorage.getItem("ADM");
  public selected = [];
  public deleted = true;
  public chkBoxSelected = [];
  public SelectionType = SelectionType;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;

  constructor(
    private service: AssessmentService,
    private modalService: NgbModal,
    private tokenStorage: TokenStorage,
    private _coreTranslationService: CoreTranslationService,
    private _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService,
    private router: Router
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
    this.currentUserName = this.tokenStorage.getUsername();
    this.getListAssessmentIdByUsername();
    this.getListPrograms();
    this.getListStandard();
    this.getlistCrite();
    this.searchAssessment();
    this.contentHeader = {
      headerTitle: "CONTENT_HEADER.LIST_CRITERIA1",
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
            name: "MENU.REPORT",
            isLink: false,
            link: "/",
          },
          {
            name: 'MENU.REPORT',
            isLink: false,
            link: '/'
          },
          {
            name: "CONTENT_HEADER.LIST_CRITERIA1",
            isLink: false,
          },
        ],
      },
    };
    this._coreTranslationService.translate(eng, vie);
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  getRowClass = (row) => {
    return {
      assess: row.user == "" && row.viewers == "",
    };
  };
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
      title: this._translateService.instant(
        "MESSAGE.CRITERIA.DELETE_CONFIRM"
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
        let arr = [];
        let array = [];
        this.selected.map(e => { arr.push(e['id']) })
        if (this.roleAdmin == 'false') {
          arr.forEach(element => {
            if (this.listAss.includes(element)) {
              array.push(element)
            }
          })
        } else {
          array = arr;
        }
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
                  "MESSAGE.CRITERIA.DELETE_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                //load lại trang kết quả
                this.deleted= true;
                this.searchAssessment();
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
                  "MESSAGE.ASSESSMENT.CANNOT_DELETE"
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
  searchAssessment() {
    if (this.programId == null) {
      this.idPro = "";
    } else {
      this.idPro = this.programId;
    }

    if (this.directoryId == null) {
      this.idDir = "";
    } else {
      this.idDir = this.directoryId;
    }

    if (this.criteriaId == null) {
      this.idCrite = "";
    } else {
      this.idCrite = this.criteriaId;
    }
    let params = {
      method: "GET",
      keyword: this.keyword,
      lang: this._translateService.currentLang,
      reportType: 2,
      programId: this.idPro,
      directoryId: this.idDir,
      criteriaId: this.idCrite,
      currentPage: this.currentPage,
      perPage: this.perPage,
    };
    console.log(this.keyword);

    Swal.showLoading();
    this.service
      .searchAssessment(params)
      .then((data) => {
        Swal.close();
        let response: any = data;
        // console.log(data);

        if (response.code === 0) {
          // for(let i = 0; i< data.content["items"].length ; i++){
          //   let a = data.content["items"][i]["createdDate"];
          //   let b = data.content["items"][i]["updatedDate"];
          //   if(data.content["items"][i]["createdDate"] != null){
          //     data.content["items"][i]["createdDate"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0];
          //   }
          //   if(data.content["items"][i]["updatedDate"] != null && data.content["items"][i]["updatedBy"] != null){
          //     data.content["items"][i]["updatedDate"] = b.split("T")[0].split("-")[2] + "-" + b.split("T")[0].split("-")[1] + "-" + b.split("T")[0].split("-")[0];
          //   } else {
          //     data.content["items"][i]["updatedDate"] = "";
          //   }
          // }
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

  openAssessment(id) {
    let xMax = window.outerWidth;
    let yMax = window.outerHeight;
    window.open(
      this.service.getURLEditor(id),
      "assessment",
      "width=" + xMax + ", height=" + yMax + ",left=0,top=0"
    );
  }

  editAssessment(id, modalSLCIM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSLCIM, {
      size: "xl",
      scrollable: true,
    });
  }
  idDetail;
  ObjDetail;
  detailAssessment(assessment, modalSM) {
    this.idDetail = assessment.id;
    this.ObjDetail = assessment;
    this.modalService.open(modalSM, {
      centered: true,
      size: "lg",
    });
  }

  deleteAssessment(id: number) {
    Swal.fire({
      title: this._translateService.instant(
        "MESSAGE.ASSESSMENT.DELETE_CONFIRM"
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
          .deleteAssessment(params, id)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant(
                  "MESSAGE.ASSESSMENT.DELETE_SUCCESS"
                ),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
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

  afterCreateAssessment() {
    this.modalService.dismissAll();
    this.searchAssessment();
  }

  afterEditAssessment() {
    this.modalService.dismissAll();
    this.searchAssessment();
  }

  openModalAddAssessment(modalSLCIM) {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .createNewFile(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          window.localStorage.removeItem("idA");
          window.localStorage.setItem("idA", response.content);
          console.log("Content: ", response.content);
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

    this.modalService.open(modalSLCIM, {
      size: "xl", // size: 'xs' | 'sm' | 'lg' | 'xl'
      scrollable: true,
    });
  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchAssessment();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchAssessment();
  }

  async getListAssessmentIdByUsername() {
    let params = {
      method: "GET"
    };
    Swal.showLoading();
    await this.service
      .getListAssessmentIdByUsername(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listAss = response.content;
          console.log("oke = " + this.listAss);

        } else {
          this.listAss = [];
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
    this.listAss.forEach(ele => {
      if (row.id == ele) {
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

  getListPrograms() {
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
          console.log(this.listPrograms + " =oke");

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

  onChange() {
    this.getListStandard();
    // this.standardId = null;
    this.disableSta = false;
    this.searchAssessment();
  }

  onChangeSta() {
    this.getListCrite();
    this.disableCrite = false;
    this.searchAssessment();


  }

  getListCrite() {
    if (this.directoryId == null) {
      this.idDir = "";
    } else {
      this.idDir = this.directoryId;
    }

    let params = {
      method: "GET",
      standardId: this.idDir,

    };

    Swal.showLoading();
    this.service
      .getListCritebySta(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listCrite = data.content;
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

  getListStandard() {
    if (this.programId == null) {
      this.idPro = "";
    } else {
      this.idPro = this.programId;
    }


    let params = {
      method: "GET",
      programId: this.idPro,

    };
    Swal.showLoading();
    this.service
      .getListStabyPrograms(params)
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
  resetProgram() {
    this.disableSta = true;
  }

  resetSta() {
    this.disableCrite = true;
  }

  getlistCrite() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListCrite(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listCrite = data.content;


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
}
