import { ChangeLanguageService } from './../../../services/change-language.service';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { FormService } from '../form.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { StatisticalService } from 'app/academy-database/statistical/statistical.service';

@Component({
  selector: 'app-form',
  templateUrl: './add-form.html',
  styleUrls: ['./add-form.scss']
})
export class AddFormComponent implements OnInit {

  @Output() afterCreateForm = new EventEmitter<string>();

  public contentHeader: object;
  public listRole = [];
  public listUnit = [];
  public listObject = [];
  public roleLoading = false;
  public unitLoading = false;
  public objectLoading = false;
  public addNewForm: FormGroup;
  public addUserFormSubmitted = false;
  public mergedPwdShow = false;
  public currentLang = this._translateService.currentLang;
  // public currentDay = new Date();

  // quannv
  public items = [];
  public addRPForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private service: FormService, public _translateService: TranslateService, 
    private _changeLanguageService: ChangeLanguageService, private statisticalService: StatisticalService) { 
      this._changeLanguageService.componentMethodCalled$.subscribe(() =>{
        this.currentLang = this._translateService.currentLang;
      })
    }


  ngOnInit(): void {
    this.initForm();
    this.getListUnit();
  }

  initForm() {
    this.addNewForm = this.formBuilder.group(
      {
        name: ['', [Validators.required]],
        nameEn: [''],
        yearOfApplication: [null, [Validators.required]],
        fileName: ['', [Validators.required]],
        uploadTime: ['', [Validators.required]],
        units: [null, [Validators.required]],
        numTitle: [null, [Validators.required, Validators.pattern('^[1-9][0-9]*$')]],
        startTitle: [null, [Validators.required, Validators.pattern('^[1-9][0-9]*$')]],
        pathFile: ''
      },
    );
  }

  get AddNewForm() {
    return this.addNewForm.controls;
  }

  addForm() {
    this.uploadForm();
  }

  dataSource
  uploadForm() {
    this.addUserFormSubmitted = true;

    if (this.addNewForm.value.name !== '') {
      this.addNewForm.patchValue({
        name: this.addNewForm.value.name.trim()
      })
    }
    if (this.addNewForm.invalid) {
      return;
    }
    let inputDate = new Date(this.addNewForm.value.uploadTime);
    let date = new Date();
    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);
    if (inputDate.getTime() < date.getTime()) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.FORM.INVALID_YEAR'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      }).then((result) => {
      });
      return;
    }
    console.log('year ' + this.addNewForm.value.yearOfApplication ? this.addNewForm.value.yearOfApplication + 0 : 0);

    Swal.showLoading();
    this.service.uploadForm({
      content: {
        formName: this.addNewForm.value.name,
        year: this.addNewForm.value.yearOfApplication ? this.addNewForm.value.yearOfApplication + 0 : 0,
        numTitle: this.addNewForm.value.numTitle,
        isForm: true,
        formKey: "",
        startTitle: this.addNewForm.value.startTitle,
      }
    }, this.attachments).then((data) => {
      Swal.close();
      if (data.body.code === 0) {
        this.dataSource = data.body.content;
        this.createForm();
      }
    });
  }

  createForm() {
    let content = this.addNewForm.value;
    content.fileName = this.attachments.name;
    // content.pathFile = "D:\\nong_nghiep\\hoc-vien-nong-nghiep\\DocumentManagement\\DATA_SOURCE.xlsx"; // sửa lại lấy pathFile từ api trước
    content.pathFile = this.dataSource.pathFile;
    content.numTitle = this.dataSource.numTitle;
    content.formKey = this.dataSource.formKey;
    content.startTitle = this.dataSource.startTitle;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addForm(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.FORM.ADD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterCreateForm.emit('completed');
          });

          // quannv
          let params = {
            method: "GET"
          };
          this.statisticalService.getLabelByKeyForm(params, data.content["formKey"])
          .then((label) => {
            let response = label;
          if (response.code === 0) {
            this.items = [];
            for(let i=0; i<label.content.length; i++){
              this.items.push({
                itemId: '',
                itemName: label.content[i]["value"],
                itemNameEn: '',
                itemQuantity: null,
                itemCost: null,
                itemUnit: false,
                itemSynthetic: false,
                itemRadio: 'CSDL',
                isCollapsed: true,
                itemDb: [{itemDbId: '', itemDbName: data.content["id"], itemDbYear: '',}],
                itemLabel: label.content,
                itemFieldDb: [{itemFieldId: '', itemFieldName: label.content[i]["id"], itemFieldCalculate: null,}],
                itemFieldRC: [],
                itemCalculate: null,
              });
            }

            this.addRPForm = this.formBuilder.group({
              name: [data.content["name"]],
              nameEn:[data.content["nameEn"]],
              isNo: [false],
              items: [JSON.stringify(this.items)],
              formId: [data.content["id"]]
            })

            let params = {
              method: "POST",
              content: this.addRPForm.value,
            };
            this.statisticalService.statisticalReport(params);

          }
          })
          // quannv end//

        } else if(response.code === 3){

          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.FORM.FORM_EXISTED'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          })
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

  resetForm() {
    this.ngOnInit();
    this.addNewForm.reset();
  }

  getListUnit() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getUnit(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listUnit = data.content;
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

  attachments
  onFileChange(event) {
    if (event.target.files.length > 0) {
      this.attachments = event.target.files[0];
    }
  }

}
