import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { OrganizationService } from '../organization.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss']
})
export class EditComponent implements OnInit {

  @Output() afterEdit = new EventEmitter<string>();

  public contentHeader : object;
  public addForm : FormGroup;
  public addFormSubmitted = false;
  public data;
  public json = require('feather-icons/dist/icons.json');
  public id;
  public status;
  public searchText;
  public chooseFeather = "";
  public listCategory = [];
  standardSet = null;
  public organization;
  disableUrl = true;
  sunmitted = true;
  public currentLang = this._translateService.currentLang;

  constructor(private _changeLanguageService: ChangeLanguageService,private formBuilder: FormBuilder, private service: OrganizationService,  private ref: ChangeDetectorRef,private modalService: NgbModal,public _translateService: TranslateService) { 
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
    })

  }

  ngOnInit(): void {
      this.disableUrl = true;
      this.contentHeader = {
          headerTitle: 'Cập nhật menu',
          actionButton: true,
          breadcrumb: {
              type: '',
              links: [
                  {
                      name: 'Trang chủ',
                      isLink: true,
                      link: '/'
                  },
                  {
                      name: 'Quản trị hệ thống',
                      isLink: true,
                      link: '/'
                  },
                  {
                      name: 'Quản lý menu',
                      isLink: false
                  }
              ]
          }
      };
      this.id = window.localStorage.getItem("id");
      this.initFrom();
      this.getListCategory();
      this.getMenusDetail();
  }

  initFrom() {
      this.addForm = this.formBuilder.group(
          { id: ['',Validators.required],
          name: ['',[Validators.required]],
          encode: ['true', [Validators.required]],
          description: ['',],
          // category: [null, Validators.required],
          categories:[null,Validators.required],
          nameEn:[''],
          descriptionEn: [''],
          },
      );
  }

  fillFrom(){
      this.addForm.patchValue(
          {
            description:this.data.description,
            id:this.data.id,
            name: this.data.name,
            encode: String(this.data.encode),
            // category:this.data.categoryId,
            categories:this.data.categories,
            nameEn:this.data.nameEn,
            descriptionEn:this.data.descriptionEn,
             
          },
      );
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

  async getMenusDetail(){
      if (this.id !== ''){
          let params = {
              method: "GET"
          };
          Swal.showLoading();
          await this.service
              .detailUser(params, this.id)
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
  editProgram(){
      this.addFormSubmitted = true;

      if (this.addForm.value.name !==''){
          this.addForm.patchValue({
              name : this.addForm.value.name.trim()
          })
      }
      if (this.addForm .invalid){
        return;
      }
      let content = this.addForm.value;
      content.status = this.addForm.value.status;
      let params = {
          method: "PUT",
          content: content
      };
      Swal.showLoading();
      this.service
          .editUser(params)
          .then((data) => {
              Swal.close();
              let response = data;
              if (response.code === 0) {
                  Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.UPDATE_SUCCESS_OR'),
                  }).then((result) => {
                      this.afterEdit.emit('completed');
                  });
              }
              else if(response.code ===3){
                Swal.fire({
                  icon: "error",
                  title: this._translateService.instant('MESSAGE.USER_MANAGEMENT.OR_EXISTED'),
                }).then((result) => {
                });
              } 
              else if(response.code ===5){
                Swal.fire({
                  icon: "error",
                  title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.DATE_EXISTED'),
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
      this.addForm.patchValue({
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
  public selectedTreeList = []
  onSelect(event) {
    this.selectedTreeList = Object.entries(event.treeModel.selectedLeafNodeIds)
      .filter(([key, value]) => {
        return (value === true);
      }).map((node) => node[0]);
  }

}



