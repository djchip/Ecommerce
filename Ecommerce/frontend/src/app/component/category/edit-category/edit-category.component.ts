import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { CategoryService } from '../category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-edit-category',
  templateUrl: './edit-category.component.html',
  styleUrls: ['./edit-category.component.scss']
})
export class EditCategoryComponent implements OnInit {

  @Input() editCategoryId;
  @Output() afterEditCategory = new EventEmitter<string>();

  public contentHeader: object;
  public editCategoryForm: FormGroup;
  public editCategoryFormSubmitted = false;
  public data;
  public unitId;
  public mergedPwdShow = false;
  public unitLoading = false;
  public currentLang = this._translateService.currentLang;
  public listCategory = [];
  public listClassify = [{ id: 1, name: "Đơn vị chức năng", nameEn: "Functional unit" },
  { id: 2, name: "Khoa", nameEn: "Faculty" },
  { id: 3, name: "Viện, Trung Tâm, Công Ty", nameEn: "Institute, Center, Company" }];
  constructor(
    private formBuilder: FormBuilder,
    private service: CategoryService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.initForm();
    this.getListCategory();
    this.getCategoryDetail();
  }

  initForm() {
    this.editCategoryForm = this.formBuilder.group(
      {
        id: ['', Validators.required],
        categoryName: ['', [Validators.required]],
        parentCategory: [''],
        description: [''],
      },
    );
  }

  fillForm() {
    this.editCategoryForm.patchValue(
      {
        id: this.data.id,
        categoryName: this.data.categoryName,
        parentCategory: this.data.parentCategory,
        description: this.data.description,
      },
    );
  }

  get EditCategoryForm() {
    return this.editCategoryForm.controls;
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
        console.log(data.content);

        if (response.code === 0) {
          this.listCategory = data.content;
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

  async getCategoryDetail() {
    if (this.editCategoryId !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailCategory(params, this.editCategoryId)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.fillForm();
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

  editCategory() {
    this.editCategoryFormSubmitted = true;

    if (this.editCategoryForm.value.categoryName !== '') {
      this.editCategoryForm.patchValue({
        categoryName: this.editCategoryForm.value.categoryName.trim()
      })
    }

    if (this.editCategoryForm.invalid) {
      return;
    }

    let content = this.editCategoryForm.value;
    if(this.editCategoryForm.value.parentCategory != null){
      content.parentId = this.editCategoryForm.value.parentCategory.id
    }

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editCategory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('Cập nhật danh mục thành công'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditCategory.emit('completed');
          });
        } else if (response.code === 100) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('Tên danh mục đã tồn tại'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
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

  resetForm() {
    this.fillForm()
  }

}
