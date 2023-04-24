import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { CategoryService } from '../category.service';
import { ChangeLanguageService } from 'app/services/change-language.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-category',
  templateUrl: './add-category.component.html',
  styleUrls: ['./add-category.component.scss']
})
export class AddCategoryComponent implements OnInit {

  @Output() afterCreateCategory = new EventEmitter<string>();

  public contentHeader: object;
  public addCategoryForm: FormGroup;
  public addCategoryFormSubmitted = false;
  public mergedPwdShow = false;
  public unitLoading = false;
  public currentLang = this._translateService.currentLang;
  public listCategory = [];
  constructor(private formBuilder: FormBuilder, 
    private service: CategoryService, 
    public _translateService: TranslateService, 
    private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.initForm();
    this.getListCategory();
  }

  initForm(){
    this.addCategoryForm = this.formBuilder.group(
      {
        categoryName: ['',[Validators.required]],
        parentCategory: [null],
        description: ['']
      },
    );
  }
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get AddCategoryForm(){
    return this.addCategoryForm.controls;
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


  addCategory(){
    this.addCategoryFormSubmitted = true;
    if(this.addCategoryForm.value.categoryName !== ''){
      this.addCategoryForm.patchValue({
        categoryName: this.addCategoryForm.value.categoryName.trim()
      })
    }
    if (this.addCategoryForm.invalid) {
      return;
    }

    let content= this.addCategoryForm.value;
    // console.log(this.addCategoryForm.value.parentCategory.id + " ID");
    
    if(this.addCategoryForm.value.parentCategory != null){
      content.parentId = this.addCategoryForm.value.parentCategory.id;
    }

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addCategory(params)
      .then( (data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('Thêm danh mục thành công'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterCreateCategory.emit('completed');
          });
        } else if(response.code === 100){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('Tên danh mục đã tồn tại'),
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
