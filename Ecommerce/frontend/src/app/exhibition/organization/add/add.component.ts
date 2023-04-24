import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { OrganizationService } from '../organization.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  styleUrls: ['./add.component.scss']
})
export class AddComponent implements OnInit {

  @Output() afterCreateUser = new EventEmitter<string>();

  public contentHeader: object;
  public listCategory = [];
  public roleLoading = false;
  public addForm: FormGroup;
  public addFormSubmitted = false;
  public mergedPwdShow = false;
  public currentLang = this._translateService.currentLang;

  constructor( private _changeLanguageService: ChangeLanguageService,private formBuilder: FormBuilder, private service: OrganizationService, public _translateService: TranslateService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
   
    })
   }

  ngOnInit(): void {
    this.initForm();
    this.getListCategory();
  }

  initForm(){
    this.addForm = this.formBuilder.group(
      {
        name: ['',[Validators.required]],
        description: ['', ],
        encode: ['true', [Validators.required]],
        // category: [null],
        nameEn:[''],
        descriptionEn: [''],
        categories:[null, Validators.required],
      },
    );
  }
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }
  get AddForm(){
    return this.addForm.controls;
  }
  
  getListCategory(){
    let params = {
      method: "GET"
    };
    this.service
      .getListCategories(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listCategory = response.content;
          
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
          if(response.code === 2){
            this.listCategory = [];
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

  add(){
    this.addFormSubmitted = true;
    if(this.addForm.value.username !== ''){
      this.addForm.patchValue({
        name: this.addForm.value.name.trim()
      })
    }
    
    if (this.addForm.invalid) {
      return;
    }

    let content= this.addForm.value;
    // content.categoryId = this.addForm.value.category.id;

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addUser(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: "Thêm mới thành công",
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateUser.emit('completed');
          });
        } else if(response.code ===3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.OR_EXISTED'),
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
