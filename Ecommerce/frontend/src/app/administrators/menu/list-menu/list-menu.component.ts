import { Component, OnInit, ViewChild,ViewEncapsulation } from '@angular/core';
import { MenuService } from '../menu.service';
import Swal from "sweetalert2";
import { ColumnMode, DatatableComponent } from '@swimlane/ngx-datatable';
import { Router } from "@angular/router";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { CoreTranslationService } from '@core/services/translation.service';
import { locale as eng } from 'assets/languages/en';
import { locale as vie } from 'assets/languages/vn';

@Component({
  selector: 'app-list-menu',
  templateUrl: './list-menu.component.html',
  styleUrls: ['./list-menu.component.scss'],
  encapsulation: ViewEncapsulation.None
  
})
export class ListMenuComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public listMenu;
  public contentHeader: object;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public totalRows = 0;
  public keyword = "";

  constructor(public _translateService: TranslateService,private service:MenuService, private router:Router, private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService
    ) { }

  ngOnInit(): void {
    this.searchMenu();
    this.contentHeader = {
      headerTitle: 'CONTENT_HEADER.MENU_LIST',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'CONTENT_HEADER.MAIN_PAGE',
            isLink: true,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.ADMINISTRATOR',
            isLink: true,
            link: '/'
          },
          {
            name: 'CONTENT_HEADER.MENU_LIST',
            isLink: false
          }
        ]
      }
    };
    this._coreTranslationService.translate(eng, vie);
  }

  searchMenu(){
    let params = {
      method: "GET", keyword:this.keyword, currentPage: this.currentPage, perPage:this.perPage
    };
    Swal.showLoading();
    this.service
      .searchMenu(params)
      .then((data) => {
        Swal.close();
        let response = data;

        if (response.code === 0) {
          this.listMenu = response.content["items"];
          this.totalRows = response.content["total"];
          // this.listMenu.forEach(element => {
          //   let parent = element.menuParentId;
          //   let parentStr = '';
          //   this.listMenu.forEach(sub => {
          //     if(sub.id == parent){
          //       parentStr = sub.menuName;
          //     }
          //   });
          //   element.menuParentId = parentStr;
          // });
        } else {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.MENU.FIND_NOT'),
          });
          if(response.code === 2){
            this.listMenu = [];
            this.totalRows = 0;
          }
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

  addMenu(){
    this.router.navigate(["/admin/menu/add-menu"]);
  }



  detailMenu(menuId, modalSM){
    window.localStorage.removeItem("menuId");
    window.localStorage.setItem("menuId", menuId);
    this.modalService.open(modalSM, {
      centered : true,
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

  editMenus(menuId, modalSM){
    window.localStorage.removeItem("menuId");
    window.localStorage.setItem("menuId", menuId);
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteMenu(id){
    Swal.fire({
      title: this._translateService.instant('MESSAGE.MENU.DELETE_CONFIRM'),
      text:  this._translateService.instant('MESSAGE.MENU.DELETE_CONFIRM1'),
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
        this.service.deleteMenu(params, id).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: "Xóa menu thành công.",
            }).then((result) => {
              //load lại trang kết quả
              this.searchMenu();
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
    this.searchMenu();
  }

  changePerpage(){
    this.searchMenu();
  }


  afterCreateMenu() {
    this.modalService.dismissAll();
    this.searchMenu();
  }

  afterEditMenu() {
    this.modalService.dismissAll();
    this.searchMenu();
  }
}
