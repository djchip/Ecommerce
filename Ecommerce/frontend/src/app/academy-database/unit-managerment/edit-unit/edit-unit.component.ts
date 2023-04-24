import { ChangeLanguageService } from 'app/services/change-language.service';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { UnitManagementService } from '../unit-managerment.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-edit-unit',
  templateUrl: './edit-unit.component.html',
  styleUrls: ['./edit-unit.component.scss']
})
export class EditUnitComponent implements OnInit {

  @Output() afterEditUnit = new EventEmitter<string>();

  public contentHeader: object;
  public addUnitForm: FormGroup;
  public addUnitFormSubmitted = false;
  public data;
  public unitId;
  public mergedPwdShow = false;
  public unitLoading = false;
  public currentLang = this._translateService.currentLang;
  // public listClassify = [{id: 1, name: "Văn phòng học viện", nameEn: "Academy office"},
  //                       {id: 2, name: "Khoa", nameEn: "Faculty"},
  //                       {id: 3, name: "Viện", nameEn: "Institute"},
  //                       {id: 4, name: "Trung tâm", nameEn: "Center"},
  //                       {id: 5, name: "Công ty", nameEn: "Company"}];
  public listClassify = [{id: 1, name: "Đơn vị chức năng", nameEn: "Functional unit"},
                        {id: 2, name: "Khoa", nameEn: "Faculty"},
                        {id: 3, name: "Viện, Trung Tâm, Công Ty", nameEn: "Institute, Center, Company"}];
  constructor(private formBuilder: FormBuilder, private service: UnitManagementService,  public _translateService: TranslateService, 
    private _changeLanguageService:ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.initForm();
    this.unitId = window.localStorage.getItem("unitId");
    this.getUnitDetail();
  }

  initForm(){
    this.addUnitForm = this.formBuilder.group(
      {
        id: ['', Validators.required],
        unitName: ['',[Validators.required]],
        unitNameEn: [''],
        unitCode: ['', [Validators.required]],
        email: ['', [Validators.required, Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)]],
        description: [''],  
        descriptionEn: [''],
        classify: [null, [Validators.required]],
      },
    );
  }

  fillForm(){
    this.addUnitForm.patchValue(
      {
        id: this.data.id,
        unitName: this.data.unitName,
        unitNameEn: this.data.unitNameEn,
        unitCode: this.data.unitCode,
        email: this.data.email,
        description: this.data.description,
        descriptionEn: this.data.descriptionEn,
        classify: this.data.classify,
      },
    );
  }

  get AddUnitForm(){
    return this.addUnitForm.controls;
  }



  async getUnitDetail(){
    if(this.unitId !== ''){
      let params = {
        method: "GET"
      };
      Swal.showLoading();
      await this.service
        .detailUnit(params, this.unitId)
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

  editUnit(){
    this.addUnitFormSubmitted = true;

    if(this.addUnitForm.value.unitName !== ''){
      this.addUnitForm.patchValue({
        unitName: this.addUnitForm.value.unitName.trim()
      })
    }

    if (this.addUnitForm.invalid) {
      return;
    }

    let content = this.addUnitForm.value;

    let params = {
      method: "PUT",
      content: content
    };
    Swal.showLoading();
    this.service
      .editUnit(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.UNIT_MANAGERMENT.UPDATE_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterEditUnit.emit('completed');
          });
        } else if(response.code === 3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.UNIT_MANAGERMENT.UNITCODE_EXISTED'),
          }).then((result) => {
          });
        } else if(response.code === 100){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.UNIT_MANAGERMENT.UNITNAME_EXISTED'),
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

  resetForm(){
    this.fillForm()
  }
}
