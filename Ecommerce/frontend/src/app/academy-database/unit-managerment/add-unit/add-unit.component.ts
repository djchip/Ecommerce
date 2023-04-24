import { ChangeLanguageService } from 'app/services/change-language.service';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { UnitManagementService } from '../unit-managerment.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-add-unit',
  templateUrl: './add-unit.component.html',
  styleUrls: ['./add-unit.component.scss']
})
export class AddUnitComponent implements OnInit {

  @Output() afterCreateUnit = new EventEmitter<string>();

  public contentHeader: object;
  public addUnitForm: FormGroup;
  public addUnitFormSubmitted = false;
  public mergedPwdShow = false;
  public unitLoading = false;
  public currentLang = this._translateService.currentLang;
  public listClassify = [{id: 1, name: "Đơn vị chức năng", nameEn: "Functional unit"},
                        {id: 2, name: "Khoa", nameEn: "Faculty"},
                        {id: 3, name: "Viện, Trung Tâm, Công Ty", nameEn: "Institute, Center, Company"}];
  constructor(private formBuilder: FormBuilder, private service: UnitManagementService, public _translateService: TranslateService, 
    private _changeLanguageService: ChangeLanguageService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(){
    this.addUnitForm = this.formBuilder.group(
      {
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
  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get AddUnitForm(){
    return this.addUnitForm.controls;
  }


  addUnit(){
    this.addUnitFormSubmitted = true;
    if(this.addUnitForm.value.unitName !== ''){
      this.addUnitForm.patchValue({
        unitName: this.addUnitForm.value.unitName.trim()
      })
    }
    if(this.addUnitForm.value.unitCode !== ''){
      this.addUnitForm.patchValue({
        unitCode: this.addUnitForm.value.unitCode.trim()
      })
    }
    if(this.addUnitForm.value.description !== ''){
      this.addUnitForm.patchValue({
        description: this.addUnitForm.value.description.trim()
      })
    }
    if (this.addUnitForm.invalid) {
      return;
    }

    let content= this.addUnitForm.value;
    content.classify = this.addUnitForm.value.classify.id;

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addUnit(params)
      .then( (data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.UNIT_MANAGERMENT.ADD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateUnit.emit('completed');
          });
        } else if(response.code === 3){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.UNIT_MANAGERMENT.UNITCODE_EXISTED'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
          });
        } else if(response.code === 100){
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.UNIT_MANAGERMENT.UNITNAME_EXISTED'),
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
