import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from './../../../../@core/services/translation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';
import { ProofManagementService } from './../proof-management.service';
import { ColumnMode, SelectionType } from '@swimlane/ngx-datatable';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import Swal from 'sweetalert2';
import { CriteriaService } from 'app/exhibition/criteria/criteria.service';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-list-proof',
  templateUrl: './list-proof.component.html',
  styleUrls: ['./list-proof.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListProofComponent implements OnInit {

  public contentHeader: object;
  public keyword = "";
  public idCri = "";
  public idSta = "";
  public idPro = "";
  public idStandard = "";
  public idProgram = "";
  public temp = [];
  public rows = [];
  public tempData = this.rows;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public listStandard = [];
  public programId = null;
  public standardId = null;
  public criteriaId = null;
  public listCriteria = [];
  public listPrograms = [];
  disableCri = true;
  disableSta = true;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public roleAdmin = window.localStorage.getItem("ADM");
  vn = "vn";
  en = "en";
  public deleted = true;
  public hasFile = true;
  public selected = [];
  public chkBoxSelected = [];
  public SelectionType = SelectionType;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;

  constructor(
    private service: ProofManagementService,
    private criteriaService: CriteriaService,
    private router: Router,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    private _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      console.log('lang' + this.currentLang == this.vn)
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
    this.searchProof();
    this.getListStandard();
    this.getListPrograms();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_PROOF',
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
            name: 'CONTENT_HEADER.PROOF_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
    this.searchProof();
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  // searchProof(){
  //   let params = {
  //     method: "GET", keyword:this.keyword, currentPage: this.currentPage, perPage:this.perPage
  //   };
  //   Swal.showLoading();
  //   this.service
  //     .searchProof(params)
  //     .then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         for(let i = 0; i< data.content["items"].length ; i++){
  //           if(data.content["items"][i]["updatedBy"] == '' || data.content["items"][i]["updatedBy"] == null){
  //             data.content["items"][i]["updatedDate"] = '';
  //           }else {
  //             let updatedDate = data.content["items"][i]["updatedDate"];
  //             data.content["items"][i]["updatedDate"] = updatedDate.split("T")[0].split("-")[2] + "-" + updatedDate.split("T")[0].split("-")[1] + "-" + updatedDate.split("T")[0].split("-")[0];
  //           }
  //           let createdDate = data.content["items"][i]["createdDate"];
  //           data.content["items"][i]["createdDate"] = createdDate.split("T")[0].split("-")[2] + "-" + createdDate.split("T")[0].split("-")[1] + "-" + createdDate.split("T")[0].split("-")[0];
  //         }
  //         this.rows = response.content["items"];
  //         this.totalRows = response.content["total"];
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.FIND_NOT'),
  //         });
  //         if(response.code === 2){
  //           this.rows = [];
  //           this.totalRows = 0;
  //         }
  //       }
  //     })
  //     .catch((error) => {
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });
  // }

  fillForm() {

  }

  searchProof() {
    if (this.programId == null) {
      this.idPro = "";
    } else {
      this.idPro = this.programId;
    }
    if (this.standardId == null) {
      this.idSta = "";
    } else {
      this.idSta = this.standardId;
    }

    if (this.criteriaId == null) {
      this.idCri = "";
    } else {
      this.idCri = this.criteriaId;
    }
    let params = {
      method: "GET",
      keyword: this.keyword,
      programId: this.idPro,
      standardId: this.idSta,
      criteriaId: this.idCri,
      currentPage: this.currentPage,
      perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchProof(params)
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
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: "OK",
        });
      });
  }

  editProof(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });
  }

  copyProof(id, modalSM) {
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl'
    });
  }

  deleteProof(id: number) {
    Swal.fire({
      title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.DELETE_CONFIRM'),
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
        this.service.deleteProof(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchProof();
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
      title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.DELETE_CONFIRM'),
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
        this.selected.map(e => { arr.push(e['id']) })
        let params = {
          method: 'DELETE', content: arr,
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.deleted= true;
              this.searchProof();
            });
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

  onChangePro() {
    this.getListStandard();
    this.disableSta = false;
    this.disableCri = true;
    this.standardId = null;
    this.criteriaId = null;
    this.searchProof();
  }

  onChangeStandard() {
    this.searchProof();
    this.getListCriteriaByStandard();
    this.disableCri = false;
    this.criteriaId = null;
  }

  onChangeCriteria() {
    this.searchProof();
  }

  resetPro() {
    this.programId = null;
    this.standardId = null;
    // this.listStandard;
    this.searchProof();
    this.disableSta = true;
    this.disableCri = true;
    
  }

  resetSta(){ 
    this.disableCri = true;
    this.searchProof();
  }

  // autoUpload(){
  //   {
  //     Swal.fire({
  //       title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.CONFIRM_AUTO'),
  //       icon: 'warning',
  //       showCancelButton: true,
  //       confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
  //       customClass: {
  //         confirmButton: 'btn btn-primary',
  //         cancelButton: 'btn btn-danger ml-1'
  //       }
  //     }).then((result) => {
  //       if (result.value) {
  //         let params = {
  //           method: 'POST'
  //         }
  //         this.service.autoUpload(params).then((data) => {
  //           let response = data;
  //           if (response.code === 0) {
  //             Swal.fire({
  //               icon: "success",
  //               title: this._translateService.instant('MESSAGE.PROOF_MANAGEMENT.AUTO_SUCCESS'),
  //               confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //             }).then((result) => {
  //               //load lại trang kết quả
  //               this.searchProof();
  //             });
  //           } else {
  //             Swal.fire({
  //               icon: "error",
  //               title: response.errorMessages,
  //             });
  //           }
  //         })
  //         .catch((error) => {
  //           Swal.close();
  //           Swal.fire({
  //             icon: "error",
  //             title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //             confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //           });
  //         });
  //       }
  //     });
  //   }
  // }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchProof();
  }

  changePerpage() {
    this.currentPage = 0;
    this.searchProof();
  }

  openModalAddProof(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }
  idDetail
  ObjDetail
  detailProof(proof, modalSM) {
    this.idDetail = proof.id;
    this.ObjDetail = proof;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg'
    });
  }

  afterCreateProof() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchProof();
  }

  afterEditProof() {
    this.modalService.dismissAll();
    this.currentPage = 0
    this.searchProof();
  }

  onExportToPDF() {

  }

  onExportToCsv() {

  }

  onExportToExcel() {

  }

  getListPrograms() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.criteriaService
      .getListPrograms(params)
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

  getListStandard() {
    if (this.programId == null) {
      this.idProgram = "";
    } else {
      this.idProgram = this.programId;
    }
    let params = {
      method: "GET", programId: this.idProgram,
    };
    Swal.showLoading();
    this.criteriaService
      .getListStaByProgramId(params)
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

  getListCriteriaByStandard() {
    if (this.standardId == null) {
      this.idStandard = "";
    } else {
      this.idStandard = this.standardId;
    }
    let params = {
      method: "GET", standardId: this.idStandard,
    };
    Swal.showLoading();
    this.service
      .getListCriteriaByStandard(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listCriteria = data.content;
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

}
