import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { CategoriesManagementService } from '../categories-management.service';

@Component({
  selector: 'app-add-categories',
  templateUrl: './add-categories.component.html',
  styleUrls: ['./add-categories.component.scss']
})
export class AddCategoriesComponent implements OnInit {

  @Output() afterCreateCategories = new EventEmitter<string>();

  public contentHeader: object;
  public listCategories = [];
  public categoriesForm: FormGroup;
  public addCategoriesFormSubmitted = false;
  public mergedPwdShow = false;
  public json = require('feather-icons/dist/icons.json');
  public data;
  public searchText;
  public chooseFeather = "";
  disableUrl = true;
  sunmitted = true;

  constructor(private formBuilder: FormBuilder,
              private service: CategoriesManagementService,
              public _translateService: TranslateService,
              private modalService: NgbModal,
              private ref: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.disableUrl = true;
    this.data = this.json;
    this.contentHeader = {
      headerTitle: 'Thêm mới Programs',
      actionButton: true,
    };
    this.categoriesForm = this.formBuilder.group(
      {
        description: [''],
        name: ['',Validators.required],
        // note:[''],
        nameEn:[''],
        descriptionEn: [''],

      },
    );
  }

  addCategories(){
    this.addCategoriesFormSubmitted = true;
    if (this.categoriesForm.value.name !== '') {
      this.categoriesForm.patchValue({
        name: this.categoriesForm.value.name.trim()
      })
    }
    if (this.categoriesForm.invalid){
      return;
    }
    

    let content= this.categoriesForm.value;
    console.log(content);
    
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addCategories(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.ADD_SUCCESS'),
          }).then((result) => {
            this.afterCreateCategories.emit('completed');
          });
          
        } else if(response.code ===3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.NAME_EXISTED'),
          }).then((result) => {
          });
        } 
     
        else if(response.code ===5){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.DATE_EXISTED'),
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

  get AddCategoriesFrom(){
    return this.categoriesForm.controls;
  }

  // modal Open Small
  modalOpenSM(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  copy(value) {
    this.categoriesForm.patchValue({
      icon: value
    })
    this.chooseFeather = value;
    this.ref.detectChanges();
  }

  onChange(event){
    this.disableUrl = false
  }

  resetCalculations(){
    this.disableUrl = true;
  }

  resetForm(){
    this.ngOnInit();
    this.categoriesForm.reset();
    this.sunmitted = false;

  }
  }


