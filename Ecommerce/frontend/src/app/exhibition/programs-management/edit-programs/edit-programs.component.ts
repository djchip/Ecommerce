import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';
import { ProgramService } from '../programs.service';
import { TranslateService } from '@ngx-translate/core';
import { ChangeLanguageService } from 'app/services/change-language.service';

@Component({
  selector: 'app-edit-programs',
  templateUrl: './edit-programs.component.html',
  styleUrls: ['./edit-programs.component.scss']
})
export class EditProgramsComponent implements OnInit {

  @Output() afterEditPrograms = new EventEmitter<string>();

  public contentHeader : object;
  public listPrograms = [{id:"1", name: "Level 1"}, {id:"2", name: "Level 2"}, {id:"3", name: "Level 3"}];
  public addProgramsFrom : FormGroup;
  public addProgramsFormSubmitted = false;
  public data;
  public json = require('feather-icons/dist/icons.json');
  public listIcon;
  public id;
  public status;
  public searchText;
  public chooseFeather = "";
  public listOrganization = [];
  standardSet = null;
  public organization;
  disableUrl = true;
  public listCat=[];
  sunmitted = true;
  public currentLang = this._translateService.currentLang;

  constructor(private _changeLanguageService: ChangeLanguageService,private formBuilder: FormBuilder, private service: ProgramService, private modalService: NgbModal, private ref: ChangeDetectorRef,    public _translateService: TranslateService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
      this.currentLang = this._translateService.currentLang;
      // this.searchCategories();
    })
   }

  ngOnInit(): void {
      this.listIcon = this.json;
      this.disableUrl = true;
    //   this.getListProgram();
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
      this.getLisCategories();
      this.getListOrganization()
      this.getMenusDetail();
  }

  initFrom() {
      this.addProgramsFrom = this.formBuilder.group(
          { id: ['',Validators.required],
            name: ['',Validators.required],
            description: [''],
            // note:[''],
            organizationId:['',Validators.required],
            nameEn:[''],
            descriptionEn: [''],
            categoryId:['',Validators.required],

    

          },
      );
  }

  fillFrom(){
      this.addProgramsFrom.patchValue(
          {
            description:this.data.description,
            // note:this.data.note,
            id:this.data.id,
            name: this.data.name,
            organizationId:this.data.organizationId,
            nameEn:this.data.nameEn,
            descriptionEn:this.data.descriptionEn,
            categoryId: this.data.categoryId,

             
          },
      );
  }

  get AddProgramFrom(){
      return this.addProgramsFrom.controls;
  }
  getLisCategories() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getLisCategories1(params)
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

  getListOrganization(){
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListOrganization(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listOrganization = data.content;
          // this.getMenusDetail();
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

  async getMenusDetail(){
      if (this.id !== ''){
          let params = {
              method: "GET"
          };
          Swal.showLoading();
          await this.service
              .detailProgram(params, this.id)
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
      this.addProgramsFormSubmitted = true;

      if (this.addProgramsFrom.value.name !==''){
          this.addProgramsFrom.patchValue({
              name : this.addProgramsFrom.value.name.trim()
          })
      }
      if (this.addProgramsFrom .invalid){
        return;
      }
      let content = this.addProgramsFrom.value;
      content.status = this.addProgramsFrom.value.status;
      // content.organizationId = this.addProgramsFrom.value.organization.id;
      // content.categoryId = this.addProgramsFrom.value.categoryId.id;
      let params = {
          method: "PUT",
          content: content
      };
      Swal.showLoading();
      this.service
          .editPrograms(params)
          .then((data) => {
              Swal.close();
              let response = data;
              if (response.code === 0) {
                  Swal.fire({
                      icon: "success",
                      title: this._translateService.instant('MESSAGE.PROGRAM_MANAGEMENT.UPDATE_SUCCESS'),
                  }).then((result) => {
                      this.afterEditPrograms.emit('completed');
                  });
              }
              else if(response.code ===3){
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
      this.addProgramsFrom.patchValue({
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
