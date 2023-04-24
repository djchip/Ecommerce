import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { CoreTranslationService } from '@core/services/translation.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TranslateService } from '@ngx-translate/core';
import { DatatableComponent, ColumnMode, SelectionType } from '@swimlane/ngx-datatable';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { CategoryService } from '../category.service';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class CategoryComponent implements OnInit {

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
  public deleted = true;
  public messages;
  public privileges = JSON.parse(localStorage.getItem("action"));
  public acceptAction: any;
  constructor(
    private formBuilder: FormBuilder,
    private _changeLanguageService: ChangeLanguageService,
    private service: CategoryService,
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
      this.searchCategory();
    })

  }

  ngOnInit(): void {
    this.privileges.forEach(element => {
      if (this.router.url === element.url) {
        this.acceptAction = element.action;
      }
    });
    this.searchCategory();
    this.contentHeader = {
      headerTitle: 'Danh sách danh mục',
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
            name: 'Quản lí danh mục',
            isLink: false,
            link: '/'
          },
        ]
      }
    };
    this.proForm = this.formBuilder.group({
      categories: ['']
    })
    this.messages = {
      emptyMessage: this._translateService.instant('LABEL.NO_DATA'),
      totalMessage: this._translateService.instant('LABEL.TOTAL')
    };
  }

  searchCategory() {
    let params = {
      method: "GET",
      keyword: this.keyword,
      currentPage: this.currentPage,
      perPage: this.perPage
    };
    Swal.showLoading();
    this.service
      .searchCategory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listCategories = response.content["items"];
          this.totalRows = response.content["total"];
        } else {
          if (response.code === 2) {
            this.listCategories = [];
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

  addCategories() {
    this.router.navigate(["/admin/categories/add-categories"]);
  }

  categoryId
  openModalDetailCategory(id, modalSM) {
    this.categoryId = id;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }



  // modal Open Small
  openModalAddCategory(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  editCategoryId
  editCategory(id, modalSM) {
    this.editCategoryId = id;
    this.modalService.open(modalSM, {
      centered: true,
      size: 'lg' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  deleteCategory(id: number) {
    Swal.fire({
      title: this._translateService.instant('Bạn có chắc chắn muốn xóa danh mục này'),
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
          .deleteCategory(params, id)
          .then((data) => {
            let response = data;
            if (response.code === 0) {
              Swal.fire({
                icon: "success",
                title: this._translateService.instant('Xóa danh mục thành công'),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                this.searchCategory();
              });
            } else if (response.code === 12) {
              Swal.fire({
                icon: "warning",
                title: this._translateService.instant('Danh mục đang được sử dụng, vui lòng không xóa'),
                confirmButtonText:
                  this._translateService.instant("ACTION.ACCEPT"),
              }).then((result) => {
                this.searchCategory();
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

  setPage(pageInfo) {
    this.currentPage = pageInfo.offset;
    this.searchCategory();
  }

  changePerpage() {
    this.currentPage = 0
    this.searchCategory();
  }

  afterCreateCategory() {
    this.modalService.dismissAll();
    this.searchCategory();
  }

  afterEditCategory() {
    this.modalService.dismissAll();
    this.searchCategory();
  }

  deleteMultipleCategory() {
    Swal.fire({
      title: this._translateService.instant('Bạn có chắc chắn muốn xóa danh mục này'),
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
        this.selected.map((e) => {
          arr.push(e["id"]);
        });
        let params = {
          method: "DELETE",
          content: arr,
        };
        this.service.deleteMulti(params).then((data) => {
          let response = data;
          if (response.code === 0) {
            Swal.fire({
              icon: "success",
              title: this._translateService.instant('Xóa danh mục thành công'),
              confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
            }).then((result) => {
              this.deleted = true;
              this.searchCategory();
            });
          } else if (response.code === 12) {
            Swal.fire({
              icon: "warning",
              title: this._translateService.instant('Danh mục đang được sử dụng, vui lòng không xóa'),
              confirmButtonText:
                this._translateService.instant("ACTION.ACCEPT"),
            }).then((result) => {
              this.deleted = true;
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
    if (this.selected.length > 0) {
      this.deleted = false;
      console.log("oke " + this.deleted.valueOf)

    } else {
      this.deleted = true;
      console.log("oke 5" + this.deleted.valueOf)

    }

  }

}
