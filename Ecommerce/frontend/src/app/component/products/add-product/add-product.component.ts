import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { CategoryService } from 'app/component/category/category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';
import { ProductService } from '../product.service';
import { Observable } from 'rxjs/internal/Observable';

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.scss']
})
export class AddProductComponent implements OnInit {

  @Output() afterCreateProduct = new EventEmitter<string>();

  selectedFiles?: FileList;
  selectedFileNames: string[] = [];

  progressInfos: any[] = [];
  message: string[] = [];

  previews: string[] = [];
  imageInfos?: Observable<any>;

  public contentHeader: object;
  public addProductForm: FormGroup;
  public addProductFormSubmitted = false;
  public mergedPwdShow = false;
  public unitLoading = false;
  public currentLang = this._translateService.currentLang;
  public listCategories = [];
  constructor(private formBuilder: FormBuilder,
    private service: ProductService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.initForm();
    this.getListCategory();
  }

  initForm() {
    this.addProductForm = this.formBuilder.group(
      {
        name: ['', [Validators.required]],
        category: [null, [Validators.required]],
        description: [''],
        unitPrice: ['', [Validators.required, Validators.pattern('^[1-9][0-9]*$')]],
        discount: ['', Validators.pattern('^(0|[1-9][0-9]*)$')],
        unitsInStock: ['', [Validators.required, Validators.pattern('^(0|[1-9][0-9]*)$')]],
        featured: ['true', [Validators.required]],
        imgProduct: ['', [Validators.required]]
      },
    );
  }

  reset() {
    this.previews = [];
  }

  selectFiles(event: any): void {
    this.message = [];
    this.progressInfos = [];
    this.selectedFileNames = [];
    this.selectedFiles = event.target.files;

    this.previews = [];
    if (this.selectedFiles && this.selectedFiles[0]) {
      const numberOfFiles = this.selectedFiles.length;
      for (let i = 0; i < numberOfFiles; i++) {
        const reader = new FileReader();

        reader.onload = (e: any) => {
          console.log(e.target.result);
          this.previews.push(e.target.result);
        };

        reader.readAsDataURL(this.selectedFiles[i]);

        this.selectedFileNames.push(this.selectedFiles[i].name);
      }
    }
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

  get AddProductForm() {
    return this.addProductForm.controls;
  }


  idProductAdd
  addProduct() {
    this.addProductFormSubmitted = true;
    if (this.addProductForm.value.name !== '') {
      this.addProductForm.patchValue({
        name: this.addProductForm.value.name.trim()
      })
    }
    if (this.addProductForm.invalid) {
      return;
    }
    let content = this.addProductForm.value;

    let params = {
      method: "POST",
      content: content
    };
    Swal.showLoading();
    this.service
      .addProduct(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.idProductAdd = response.content.id;
          let paramsImg = {
            method: "POST",
            content: {id: this.idProductAdd}
          };
          this.service.addProductImg(paramsImg, this.selectedFiles)
            .then((dataImg) => {
              Swal.close();
              let responseImg = dataImg;
              if (responseImg.code === 0) {
                Swal.fire({
                  icon: "success",
                  title: this._translateService.instant('Thêm sản phẩm thành công'),
                  confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                }).then((result) => {
                  this.afterCreateProduct.emit('completed');
                });
              } else {
                Swal.fire({
                  icon: "error",
                  title: this._translateService.instant('Đã có lỗi xảy ra'),
                  confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                })
              }
            })

        } else if (response.code === 100) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('Tên sản phẩm đã tồn tại'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
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
          title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
          confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        });
      });

  }

}
