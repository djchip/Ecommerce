import { FormService } from "./../form.service";
import {
  Component,
  OnInit,
  Output,
  EventEmitter,
  ViewEncapsulation,
} from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  Validators,
} from "@angular/forms";
import Swal from "sweetalert2";
import { TranslateService } from "@ngx-translate/core";
import { ChangeLanguageService } from "app/services/change-language.service";
import { StatisticalService } from "app/academy-database/statistical/statistical.service";

@Component({
  selector: "app-copy-form",
  templateUrl: "./copy-form.component.html",
  styleUrls: ["./copy-form.component.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class CopyFormComponent implements OnInit {
  @Output() afterCreateProof = new EventEmitter<string>();

  public contentHeader: object;
  public copyFG: FormGroup;
  public addUserFormSubmitted = false;
  public data;
  public form_id;
  public listObject = [];
  // public listRole = [];
  public listUnit = [];
  public currentLang = this._translateService.currentLang;
  public items = [];
  public addRPForm: FormGroup;

  constructor(
    private _changeLanguageService: ChangeLanguageService,
    private formBuilder: FormBuilder,
    private service: FormService, private statisticalService: StatisticalService,
    public _translateService: TranslateService, 
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    this.form_id = window.localStorage.getItem("form_id");
    this.getListUnit();
    this.initForm();
    this.getFormDetail();
  }

  initForm() {
    this.copyFG = this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required]],
      nameEn: [''],
      yearOfApplication: [ null, [Validators.required]],
      uploadTime: [ '' , [Validators.required]],
      units: [null, [Validators.required]],
    });
  }

  fillForm() {
    this.copyFG.patchValue({
      name: this.data.name,
      nameEn: this.data.nameEn,
      units: this.data.units,
      fileName: this.data.fileName,
    })
  }

  getListUnit() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getUnit(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          Swal.close();
          this.listUnit = data.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      .catch((error) => {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  getFormDetail() {
    if (this.form_id) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service.detailForm(params, this.form_id).then((data) => {
        let response = data;
        if (response.code == 0) {
            Swal.close();
            this.data = response.content;
            this.fillForm();
        }
      }).catch((error) => {
        Swal.fire({
          icon: "error",
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
    }
  }

  dataSource
  uploadForm() {
    this.addUserFormSubmitted = true;
    if (this.copyFG.value.name !== '') {
      this.copyFG.patchValue({
        name: this.copyFG.value.name.trim()
      })
    }
    if (this.copyFG.invalid) {
      return;
    }
    let inputDate = new Date(this.copyFG.value.uploadTime);
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
    Swal.showLoading();
    this.createForm();
    // this.service.uploadForm({ 
    //   content: { 
    //     formName: this.copyFG.value.name, 
    //     year: this.copyFG.value.yearOfApplication ? this.copyFG.value.yearOfApplication + 0 : 0,
    //     numTitle: this.copyFG.value.numTitle,
    //     isForm: true,
    //     formKey: "",
    //   }}, this.attachments).then((data) => {
    //   Swal.close();
    //   if (data.body.code === 0) {
    //     this.dataSource = data.body.content;
    //     this.createForm();
    //   }
    // });
  }

  createForm(){
    let content = this.copyFG.value;
    content.id = this.form_id;
    // content.fileName = this.attachments.name;
    // content.pathFile = "D:\\nong_nghiep\\hoc-vien-nong-nghiep\\DocumentManagement\\DATA_SOURCE.xlsx"; // sửa lại lấy pathFile từ api trước
    // content.pathFile = this.dataSource.pathFile;
    // content.numTitle = this.dataSource.numTitle;
    // content.formKey = this.dataSource.formKey;
    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service.copyForm(params).then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.FORM.COPY_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            this.afterCreateProof.emit('completed');
          });
          //quannv
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
          // end
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
    this.fillForm();
  }

  attachments
  onFileChange(event) {
    if (event.target.files.length > 0) {
      this.attachments = event.target.files[0];
    }
  }

  get CopyForm() {
    return this.copyFG.controls;
  }
}
