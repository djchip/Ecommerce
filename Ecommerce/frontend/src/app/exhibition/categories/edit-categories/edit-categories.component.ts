import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { TranslateService } from '@ngx-translate/core';
import { CategoriesManagementService } from '../categories-management.service';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-edit-categories',
  templateUrl: './edit-categories.component.html',
  styleUrls: ['./edit-categories.component.scss']
})
export class EditCategoriesComponent implements OnInit {
  @Output() afterEditCategories = new EventEmitter<string>();

  public contentHeader : object;
  public addCategoriesFrom : FormGroup;
  public addCategoriesFormSubmitted = false;
  public data;
  public json = require('feather-icons/dist/icons.json');
  public listIcon;
  public id;
  public status;
  public searchText;
  public chooseFeather = "";
  public currentLang = this._translateService.currentLang;
  constructor(private _changeLanguageService: ChangeLanguageService,private formBuilder: FormBuilder, private service: CategoriesManagementService, private modalService: NgbModal, private ref: ChangeDetectorRef,    public _translateService: TranslateService) 
  { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
        
      })

  }


  ngOnInit(): void {
      this.listIcon = this.json;
      this.id = window.localStorage.getItem("id");
      this.initFrom();
    this.getMenusDetail();
  }

  initFrom() {
      this.addCategoriesFrom = this.formBuilder.group(
          { 
            id: ['',Validators.required],
            name: ['',Validators.required],
            description: [''],
            // note:[''],
            nameEn:[''],
            descriptionEn: [''],

    

          },
      );
  }

  fillFrom(){
      this.addCategoriesFrom.patchValue(
          {
            description:this.data.description,
            // note:this.data.note,
            id:this.data.id,
            name: this.data.name,
             nameEn:this.data.nameEn,
             descriptionEn:this.data.descriptionEn,
          },
      );
  }

  get AddCategoriesFrom(){
      return this.addCategoriesFrom.controls;
  }


  async getMenusDetail(){
      if (this.id !== ''){
          let params = {
              method: "GET"
          };
          Swal.showLoading();
          await this.service
              .detailCategories(params, this.id)
              .then((data) => {
                  Swal.close();
                  let response = data;
                  if (response.code === 0) {
                      this.data = response.content;
                      console.log("DATA",this.data);
                      this.fillFrom();
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
                      title: "Can not reach Gateway.",
                      confirmButtonText: "OK",
                  });
              });
      }
  }

  editCategories(){
      this.addCategoriesFormSubmitted = true;

      if (this.addCategoriesFrom.value.name !==''){
          this.addCategoriesFrom.patchValue({
              name : this.addCategoriesFrom.value.name.trim()
          })
      }
      if (this.addCategoriesFrom .invalid){
        return;
      }
      let content = this.addCategoriesFrom.value;
      content.status = this.addCategoriesFrom.value.status;

      let params = {
          method: "PUT",
          content: content
      };
      Swal.showLoading();
      this.service
          .editCategories(params)
          .then((data) => {
              Swal.close();
              let response = data;
              if (response.code === 0) {
                  Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.CATEGORIES_MANAGEMENT.UPDATE_SUCCESS'),
                  }).then((result) => {
                      this.afterEditCategories.emit('completed');
                  });
              }
              else if(response.code ===3){
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
              }  else {
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
  resetForm(){
      this.fillFrom();
  }

  // modal Open Small
  modalOpenSM(modalSM) {
      this.modalService.open(modalSM, {
          centered: true,
          size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
      });
  }

  copy(value) {
      this.addCategoriesFrom.patchValue({
          icon: value
      })
      this.chooseFeather = value;
      this.ref.detectChanges();
  }

}
