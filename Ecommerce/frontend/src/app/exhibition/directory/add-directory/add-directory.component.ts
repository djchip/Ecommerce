import { ChangeLanguageService } from 'app/services/change-language.service';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { DirectoryService } from '../directory.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { ValidateName } from '../space.validator';

@Component({
  selector: 'app-directory',
  templateUrl: './add-directory.html',
  styleUrls: ['./add-directory.scss']
})
export class AddDirectoryComponent implements OnInit {

  @Output() afterCreateDirectory = new EventEmitter<string>();

  public contentHeader: object;
  public addnewDirectory: FormGroup;
  public addDirectorySubmitted = false;
  public mergedPwdShow = false;
  public dataMethod = [{ id: "Tiêu chuẩn", name: "Tiêu chuẩn" }, { id: "Tiêu chí", name: "Tiêu chí" }];
  public roleLoading = false;
  sunmitted = true;
  public listPrograms = [];
  public listCat=[];
  public listOrg = [];
  public dirLoading = false;
  selectedRole: any;
  public describe = [];
  public selection;
  public code!: string;
  public data;
  public codeSta = "00";
  public codeSta1 = "0";
  public codeEXH = "undifile";
  public disableCat=true;
  public Code = false;
  public currentLang = this._translateService.currentLang;
  public listUser = [];
  public checkTieuchuan=null;
  constructor(private formBuilder: FormBuilder, private service: DirectoryService, public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.initForm();
    this.getListOrg();
    this.getLisCategories();
    this.getListUser();
    
  }

  initForm() {
    this.addnewDirectory = this.formBuilder.group(
      {
        name: ['', [Validators.required, ValidateName]],
        nameEn: [''],
        description: [''],
        descriptionEn: [''],
        programId: [null],
        organizationId: [null, [Validators.required]],
        userInfos: [null],
        code: ['', [Validators.required]],
        categoryId:[null,Validators.required],
      },
    );
  }

  onChangeName() {
    let code = this.addnewDirectory.value.name;
    const codeS = code.split(" ").pop();
    // codeS.length < 2 ? this.codeSta = "0" + codeS : this.codeSta = codeS;
    // console.log("Code = "+ this.Code);
    if(this.Code){
      codeS.length < 2 ? this.codeSta = "0" + codeS : this.codeSta = codeS;
    }
    else {
       this.codeSta = codeS;
    }
    this.addnewDirectory.patchValue({
      code: this.codeEXH + this.codeSta
    })
    this.checkTieuchuan=this.addnewDirectory.value.name;
    // if ( !this.checkTieuchuan.includes("Tiêu chuẩn")||  !this.checkTieuchuan.includes("Công đoàn") ) {
    //   return;
    // }
    console.log(this.checkTieuchuan+" checkTieuchuann");

    

  }

  removeSpaces(control: AbstractControl) {
    if (control && control.value && !control.value.replace(/\s/g, '').length) {
      control.setValue('');
    }
    return null;
  }

  get AddNewDirectory() {
    return this.addnewDirectory.controls;
  }

  getListOrg() {
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
          this.listOrg = data.content;
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

  onChange() {
    if (this.addnewDirectory.value.organizationId) {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      this.service
        .getCodeEXH(params, this.addnewDirectory.value.organizationId.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            // this.codeEXH = data.content;

            this.data = response.content;
            this.codeEXH = this.data.name;
            this.Code = this.data.enCode;

            if(this.addnewDirectory.value.name ){
              this.onChangeName();
            }
            // if (this.Code) {
            //   this.addnewDirectory.patchValue({
            //     code: this.codeEXH + this.codeSta
            //   })
            // } else {
            //   this.addnewDirectory.patchValue({
            //     code: this.codeEXH + this.codeSta
            //   })
            // }
            this.addnewDirectory.patchValue({
              code: this.codeEXH + this.codeSta
            })

            // console.log(this.codeEXH + " = Mã code");
            // console.log(this.Code + " = encode")
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
            title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL_EXH'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          });
        }
        )
        ;
    }



     this.disableCat = false;
 
    if (this.addnewDirectory.value.organizationId.id != null) {
      let params = {
        method: "GET",
        OrgId: this.addnewDirectory.value.organizationId.id,
      };
      Swal.showLoading();
      this.service
        .getLisCategorie(params)
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

  resetPro() {
    this.addnewDirectory.patchValue({
      code: this.codeEXH + this.codeSta
    })
  }

  addDirectory() {

    
    this.addDirectorySubmitted = true;
    if (this.addnewDirectory.value.name !== '' ) {
      this.addnewDirectory.patchValue({
        name: this.addnewDirectory.value.name.trim()
      })
    }
    if ( !this.checkTieuchuan.includes("Tiêu chuẩn") ) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.DIRECTORY.CHECK_ST'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      });
      return;
    }

    if (this.addnewDirectory.value.description !== '') {
      this.addnewDirectory.patchValue({
        description: this.addnewDirectory.value.description.trim()
      })
    }
    if (this.addnewDirectory.value.code !== '') {
      this.addnewDirectory.patchValue({
        code: this.addnewDirectory.value.code.trim()
      })
    }
    if (this.addnewDirectory.invalid) {
      return;
    }
    let content = this.addnewDirectory.value;
    content.organizationId = this.addnewDirectory.value.organizationId.id;
    content.categoryId = this.addnewDirectory.value.categoryId.id;

    let params = {
      method: "POST",
      content: content,
    };
    Swal.showLoading();
    this.service
      .addDirectory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant('MESSAGE.DIRECTORY.ADD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateDirectory.emit('completed');
          });
        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.DIRECTORY.DIRECTORY_EXISTED'),
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

  getListUser() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListUser(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listUser = data.content;
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

  // resetForm(){
  //   this.ngOnInit();
  //   this.addnewDirectory.reset();
  //   this.sunmitted = false;

  // }
  getLisCategories() {
    let params = {
      method: "GET",
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

  onChangee() {
    this.disableCat = false;
 
    if (this.addnewDirectory.value.organizationId.id != null) {
      let params = {
        method: "GET",
        OrgId: this.addnewDirectory.value.organizationId.id,
      };
      Swal.showLoading();
      this.service
        .getLisCategorie(params)
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

  resetCat() {
    this.disableCat = true;
  }
}


