import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { CoreTranslationService } from '@core/services/translation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { DatatableComponent, ColumnMode, SelectionType } from '@swimlane/ngx-datatable';
import { CategoryService } from 'app/component/category/category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-list-product',
  templateUrl: './list-product.component.html',
  styleUrls: ['./list-product.component.scss']
})
export class ListProductComponent implements OnInit {

  @ViewChild(DatatableComponent) table: DatatableComponent;

  public listCategories = [];
  public listProducts = [];
  public categoryId;
  public productId;
  public keyword = "";
  public contentHeader: object;
  public ColumnMode = ColumnMode;
  public currentPage = 0;
  public perPage = 10;
  public SelectionType = SelectionType;
  public currentLang = this._translateService.currentLang;
  public dateFormat = window.localStorage.getItem("dateFormat");
  public chkBoxSelected = [];
  public totalRows = 0;
  public selected = [];
  public deleted = true;
  public messages;
  public acceptAction: any;
  constructor(
    private _changeLanguageService: ChangeLanguageService,
    private service: ProductService,
    private router: Router,
    private modalService: NgbModal,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
      this.messages = {
        emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
        totalMessage: this._translateService.instant('LABEL.TOTAL')
      };
      this.searchProduct();
    })

  }

  ngOnInit(): void {
    this.searchProduct();
    this.contentHeader = {
      headerTitle: 'Danh sách sản phẩm',
      actionButton: true,
      breadcrumb: {
        type: '',
        links: [
          {
            name: 'Trang chủ',
            isLink: true,
            link: '/dashboard'
          },
          {
            name: 'Quản lí sản phẩm',
            isLink: false,
            link: '/'
          },
        ]
      }
    };
    this.getListCategory();
    // this.proForm = this.formBuilder.group({
    //   categories: ['']
    // })
    this.messages = {
      emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
      totalMessage: this._translateService.instant('LABEL.TOTAL')
    };
  }

  getListCategory() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListCategory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listCategories = data.content;
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

  searchProduct() {
    console.log("id ", this.cateId);
    if (this.categoryId == null) {
      this.cateId = "";
    } else {
      this.cateId = this.categoryId;
    }

    let params = {
      method: "GET",
      keyword: this.keyword,
      categoryId: this.cateId,
      currentPage: this.currentPage,
      perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchProduct(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listProducts = response.content["items"];
          this.totalRows = response.content["total"];
        } else {
          if (response.code === 2) {
            this.listProducts = [];
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

  // addCategories() {
  //   this.router.navigate(["/admin/categories/add-categories"]);
  // }


  openModalDetailProduct(id, modalSM) {
    this.productId = id;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }



  // modal Open Small
  openModalAddProduct(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editProductId
  editProduct(id, modalSM) {
    this.editProductId = id;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteProduct(id: number, active: boolean) {
    if (active) {
      Swal.fire({
        title: this._translateService.instant('Bạn có chắc chắn muốn xóa sản phẩm này'),
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
            .deleteProduct(params, id)
            .then((data) => {
              let response = data;
              if (response.code === 0) {
                Swal.fire({
                  icon: "success",
                  title: this._translateService.instant('Xóa sản phẩm thành công'),
                  confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
                }).then((result) => {
                  this.searchProduct();
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
                title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
                confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
              });
            });
        }
      });
    } else {
      Swal.fire({
        title: this._translateService.instant('Bạn có chắc chắn muốn mở khóa sản phẩm này'),
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
            .deleteProduct(params, id)
            .then((data) => {
              let response = data;
              if (response.code === 46) {
                Swal.fire({
                  icon: "success",
                  title: this._translateService.instant('Mở khóa sản phẩm thành công'),
                  confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
                }).then((result) => {
                  this.searchProduct();
                });
              }
              else {
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
      });
    }

  }

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchProduct();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchProduct();
  }

  afterCreateProduct() {
    this.modalService.dismissAll();
    this.searchProduct();
  }

  afterEditProduct() {
    this.modalService.dismissAll();
    this.searchProduct();
  }

  cateId = null;
  onChange() {
    this.searchProduct();
  }

  reset() {
    this.searchProduct();
  }

  deleteMultipleProduct() {
    // Swal.fire({
    //   title: this._translateService.instant('Bạn có chắc chắn muốn xóa danh mục này'),
    //   icon: 'warning',
    //   showCancelButton: true,
    //   confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
    //   cancelButtonText: this._translateService.instant('ACTION.CANCEL'),
    //   customClass: {
    //     confirmButton: 'btn btn-primary',
    //     cancelButton: 'btn btn-danger ml-1'
    //   }
    // }).then((result) => {
    //   if (result.value) {
    //     let arr = [];
    //     this.selected.map((e) => {
    //       arr.push(e["id"]);
    //     });
    //     let params = {
    //       method: "DELETE",
    //       content: arr,
    //     };
    //     this.service.deleteMulti(params).then((data) => {
    //       let response = data;
    //       if (response.code === 0) {
    //         Swal.fire({
    //           icon: "success",
    //           title: this._translateService.instant('Xóa danh mục thành công'),
    //           confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
    //         }).then((result) => {
    //           this.deleted = true;
    //           this.searchProduct();
    //         });
    //       } else if (response.code === 12) {
    //         Swal.fire({
    //           icon: "warning",
    //           title: this._translateService.instant('Danh mục đang được sử dụng, vui lòng không xóa'),
    //           confirmButtonText:
    //             this._translateService.instant("ACTION.ACCEPT"),
    //         }).then((result) => {
    //           this.deleted = true;
    //         });
    //       }
    //     })
    //       .catch((error) => {
    //         Swal.close();
    //         Swal.fire({
    //           icon: "error",
    //           title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
    //           confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
    //         });
    //       });
    //   }
    // });
  }
  customChkboxOnSelect({ selected }) {
    this.chkBoxSelected.splice(0, this.chkBoxSelected.length);
    this.chkBoxSelected.push(...selected);
  }
  onSelect({ selected }) {
    this.selected.splice(0, this.selected.length);
    this.selected.push(...selected);
    if (this.selected.length > 0) {
      this.deleted = false;
      console.log("oke " + this.deleted.valueOf)

    } else {
      this.deleted = true;
      console.log("oke 5" + this.deleted.valueOf)

    }

  }

}
