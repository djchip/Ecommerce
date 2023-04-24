import { Component, OnInit, ViewChild ,ViewEncapsulation} from '@angular/core';
import Swal from "sweetalert2";
import { CoreTranslationService } from '@core/services/translation.service';
import { ColumnMode, DatatableComponent ,SelectionType} from '@swimlane/ngx-datatable';
import { Router } from "@angular/router";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { CategoriesManagementService } from '../categories-management.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';
import { ChangeLanguageService } from 'app/services/change-language.service';
import { FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-list-categories',
  templateUrl: './list-categories.component.html',
  styleUrls: ['./list-categories.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ListCategoriesComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public listCategories;
  public keyword = "";
  public contentHeader: object;
  public proForm: FormGroup;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public SelectionType = SelectionType;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public chkBoxSelected = [];
  public totalRows = 0;
  public selected = [];
  public deleted=true;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  constructor(private formBuilder: FormBuilder,private _changeLanguageService: ChangeLanguageService,private service:CategoriesManagementService, private router:Router, private modalService: NgbModal,  private _coreTranslationService: CoreTranslationService,public _translateService: TranslateService) 
  {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
      // document.getElementsByClassName("page-count")[0].textContent = this._translateService.instant('LABEL.TOTAL') + this.totalRows;
      this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
      this.searchCategories();
    })

   }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if(this.router.url === element.url){
        this.acceptAction = element.action;
      }
    });
    this.searchCategories();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.LIST_CATEGORIES',
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
            name: 'CONTENT_HEADER.CATEGORIES_MANAGEMENT',
            isLink: false
          }
        ]
      }
    };
    this.proForm = this.formBuilder.group({
      categories: ['']
    })
    this._coreTranslationService.translate(eng, vie);
    this.messages = {emptyMessage: this._translateService.instant('LABEL.NO_DATA'), 
        totalMessage: this._translateService.instant('LABEL.TOTAL')};
  }

  searchCategories(){
    let params = {
      method: "GET", keyword:this.keyword,lang: this._translateService.currentLang, currentPage: this.currentPage, perPage:this.perPage
    };
    Swal.showLoading();
    this.service
      .searchCategories(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          // for(let i = 0; i< data.content["items"].length ; i++){
          //   let a = data.content["items"][i]["createdDate"];

          //   data.content["items"][i]["createdDate"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0];
          // }
          // for(let i = 0; i< data.content["items"].length ; i++){
          //   let a = data.content["items"][i]["updatedDate"];
            
          //   data.content["items"][i]["updatedDate"] = a.split("T")[0].split("-")[2] + "-" + a.split("T")[0].split("-")[1] + "-" + a.split("T")[0].split("-")[0];
          // }
          this.listCategories = response.content["items"];
          this.totalRows = response.content["total"];

        } else {
          if(response.code === 2){
            this.listCategories = [];
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

  addCategories(){
    this.router.navigate(["/admin/categories/add-categories"]);
  }





  // modal Open Small
  openModalAddCategories(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editCategories(id, modalSM){
    window.localStorage.removeItem("id");
    window.localStorage.setItem("id", id);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteCategories(id:number){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.DELETE_CONFIRM'),
      // text: 'Xóa menu cha sẽ xóa luôn các menu con',
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
        this.service.deleteCategories(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.searchCategories();
            });
          } else {
            Swal.fire({
              icon: "error",
              title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.CANNOT_DELETE'),
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
    this.searchCategories();
  }

  changePerpage(){
    this.currentPage = 0
    this.searchCategories();
  }


  afterCreateCategories() {
    this.modalService.dismissAll();
    this.searchCategories();
  }

  afterEditCategories() {
    this.modalService.dismissAll();
    this.searchCategories();
  }
  idDetail
  ObjDetail
  detailCatogeries(categories, modalSM){
    this.idDetail=categories.id;
    this.ObjDetail=categories;

    this.modalService.open(modalSM, {
      centered : true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  onDeleteMulti(){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.DELETE_CONFIRM'),
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
        dto.categories = arr;
        let params = {
          method: 'DELETE', content: dto.categories,
        }
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.DELETE_SUCCESS'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              //load lại trang kết quả
              this.deleted= true;
              this.searchCategories();
            });
          } else if (response.code === 12) {
            Swal.fire(this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.BEING_USED'));
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
  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if(this.selected.length > 0){
      this.deleted = false;
      console.log("oke "+ this.deleted.valueOf)
  
    } else {
      this.deleted = true;
      console.log("oke 5"+ this.deleted.valueOf)
  
    }
  
  }
}
