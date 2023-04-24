import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { CategoryService } from 'app/component/category/category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { ProductService } from '../product.service';

@Component({
  selector: 'app-edit-product',
  templateUrl: './edit-product.component.html',
  styleUrls: ['./edit-product.component.scss']
})
export class EditProductComponent implements OnInit {

  @Input() editProductId;
  @Output() afterEditProduct = new EventEmitter<string>();

  public contentHeader: object;
  public editProductForm: FormGroup;
  public editProductFormSubmitted = false;
  public data;
  public mergedPwdShow = false;
  public unitLoading = false;
  public currentLang = this._translateService.currentLang;
  public listCategories = [];
  constructor(
    private formBuilder: FormBuilder,
    private service: ProductService,
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
    this.getProductDetail();
  }

  initForm() {
    this.editProductForm = this.formBuilder.group(
      {
        id: ['', Validators.required],
        name: ['', [Validators.required]],
        category: [null, [Validators.required]],
        description: [''],
        unitPrice: ['', [Validators.required, Validators.pattern('^[1-9][0-9]*$')]],
        discount: ['', Validators.pattern('^(0|[1-9][0-9]*)$')],
        unitsInStock: ['', [Validators.required, Validators.pattern('^(0|[1-9][0-9]*)$')]],
        featured: ['', [Validators.required]]
      },
    );
  }

  fillForm() {
    this.editProductForm.patchValue(
      {
        id: this.data.id,
        name: this.data.name,
        category: this.data.category,
        unitPrice: this.data.unitPrice,
        description: this.data.description,
        discount: this.data.discount,
        unitsInStock: this.data.unitsInStock,
        featured: String(this.data.featured),
      },
    );
  }

  get EditProductForm() {
    return this.editProductForm.controls;
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

  async getProductDetail() {
    if (this.editProductId !== '') {
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailProduct(params, this.editProductId)
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

  editProduct() {
    this.editProductFormSubmitted = true;

    if (this.editProductForm.value.name !== '') {
      this.editProductForm.patchValue({
        name: this.editProductForm.value.name.trim()
      })
    }
    // if (this.editProductForm.value.unitPrice !== '') {
    //   this.editProductForm.patchValue({
    //     unitPrice: this.editProductForm.value.unitPrice.trim()
    //   })
    // }
    // if (this.editProductForm.value.unitsInStock !== '') {
    //   this.editProductForm.patchValue({
    //     unitsInStock: this.editProductForm.value.unitsInStock.trim()
    //   })
    // }

    if (this.editProductForm.invalid) {
      return;
    }

    console.log(this.editProductForm.value.id + ' content');
    

    let content = this.editProductForm.value;

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editProduct(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('Cập nhật danh mục thành công'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditProduct.emit('completed');
          });
        }else {
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

  reset() {
    this.fillForm()
  }

}
