import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { ProgramService } from '../programs.service';
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-add-programs',
  templateUrl: './add-programs.component.html',
  styleUrls: ['./add-programs.component.scss']
})
export class AddProgramsComponent implements OnInit {

  @Output() afterCreatePrograms = new EventEmitter<string>();

  public contentHeader: object;
  public listPrograms = [];
  public programsForm: FormGroup;
  public addProgramsFormSubmitted = false;
  public mergedPwdShow = false;
  public json = require('feather-icons/dist/icons.json');
  public data;
  public searchText;
  public chooseFeather = "";
  public listCat=[];
  disableUrl = true;
  sunmitted = true;
  disableCat=true;
  public listOrganization = [];
  public currentLang = this._translateService.currentLang;

  constructor(private _changeLanguageService: ChangeLanguageService,private formBuilder: FormBuilder,
              private service: ProgramService,
              public _translateService: TranslateService,
              private modalService: NgbModal,
              private ref: ChangeDetectorRef) { 
                this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
                  this.currentLang = this._translateService.currentLang;
                })
              }

  ngOnInit(): void {
    this.disableUrl = true;
    this.data = this.json;
    this.contentHeader = {
      headerTitle: 'Thêm mới Programs',
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
            name: 'Quản lý Menu',
            isLink: false
          }
        ]
      }
    };
    this.programsForm = this.formBuilder.group(
      {
        description: [''],
        name: ['',Validators.required],
        organization:['',Validators.required],
        // note:[''],
        nameEn:[''],
        descriptionEn: [''],
        categoryId:[null,Validators.required],

      },
    );
    this.getListOrganization();
    // this.getLisCategories();

    console.log(this.programsForm.value.organization.id+"oke");
    
  }

  addPrograms(){
    this.addProgramsFormSubmitted = true;

    if (this.programsForm.value.name !== '') {
      this.programsForm.patchValue({
        name: this.programsForm.value.name.trim()
      })
    }

    if (this.programsForm.invalid){
      return;
    }
    

    let content= this.programsForm.value;
    content.organizationId = this.programsForm.value.organization.id;
    content.categoryId = this.programsForm.value.categoryId.id;

    console.log(content);
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addPrograms(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.ADD_SUCCESS'),
          }).then((result) => {
            this.afterCreatePrograms.emit('completed');
          });
          
        } else if(response.code ===3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.NAME_EXISTED'),
          }).then((result) => {
          });
        } 
        else if(response.code ===5){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.DATE_EXISTED'),
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

  getListOrganization(){
    let params = {
      method: "GET",
    };
    // this.getLisCategories();
    Swal.showLoading();
    this.service
      .getListOrganization(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrganization = data.content;
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
          title: "Không kết nối được tới hệ thống.",
          confirmButtonText: "OK",
        });
      });

  }

  get AddProgramFrom(){
    return this.programsForm.controls;
  }

  // modal Open Small
  modalOpenSM(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  copy(value) {
    this.programsForm.patchValue({
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
    this.programsForm.reset();
    this.sunmitted = false;

  }
 
  resetCat(){
    this.disableCat = true;
  }
  onChangee(){
    this.disableCat=false;
        if(this.programsForm.value.organization.id != null){

      let params = {
        method: "GET",
        OrgId: this.programsForm.value.organization.id,
      };
      Swal.showLoading();
      this.service
        .getLisCategories(params)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.listCat = data.content;
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
  
  // getLisCategories() {
  //   let params = {
  //     method: "GET",
  //     OrgId: this.programsForm.value.organization.id,
  //   };
  //   Swal.showLoading();
  //   this.service
  //     .getLisCategories(params)
  //     .then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         this.listCat = data.content;
  //       } else {
  //         Swal.fire({
  //           icon: "error",
  //           title: response.errorMessages,
  //         });
  //       }
  //     })
  //     .catch((error) => {
  //       Swal.close();
  //       Swal.fire({
  //         icon: "error",
  //         title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
  //         confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
  //       });
  //     });
  // }

}

