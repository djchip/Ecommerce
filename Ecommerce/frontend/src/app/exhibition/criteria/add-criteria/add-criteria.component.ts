import { ChangeLanguageService } from 'app/services/change-language.service';
import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { CriteriaService } from '../criteria.service';
import Swal from "sweetalert2";
import { TranslateService } from '@ngx-translate/core';
import { DirectoryService } from 'app/exhibition/directory/directory.service';

@Component({
  selector: 'app-criteria',
  templateUrl: './add-criteria.html',
  styleUrls: ['./add-criteria.scss']
})
export class AddCriteriaComponent implements OnInit {

  @Output() afterCreateDirectory = new EventEmitter<string>();
  public contentHeader: object;
  public addnewDirectory: FormGroup;
  public addUserDirectorySubmitted = false;
  public mergedPwdShow = false;
  public dataMethod = [{ id: "Ti�u chu?n", name: "Ti�u chu?n" }, { id: "Ti�u ch�", name: "Ti�u ch�" }];
  public roleLoading = false;
  sunmitted = true;
  public autoCode;
  public codeSS = "00";
  public codeSSS = "00";
  public codeEE = "00";
  public codeCri;
  public codeSta = "00";
  public codeEXH = "H";
  public listCat = [];
  public codeDir;
  public id;
  public idSta = "";
  disableSta = true;
  public currentPage = 0;
  public checkCodeinput = null;
  public perPage = 10;
  public keyword = "";
  public programId = "";
  public listStandard = [];
  public listPrograms = [];
  public listOrg = [];
  public data;
  public Code = false;
  public disableCat = true;
  public listUser = [];
  public checkTieuchi = null;
  public checkCode = 0;
  public StaById = [];
  public currentLang = this._translateService.currentLang;
  constructor(private formBuilder: FormBuilder, private service: CriteriaService, public _translateService: TranslateService, public directoryService: DirectoryService,
    private _changeLanguageService: ChangeLanguageService) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    })
  }

  ngOnInit(): void {
    this.initForm();
    this.getListUser();
    this.getListOrg();
    // this.getStabyId();

    // this.getLisCategories();
    // this.getListStandard();
    // this.getListPrograms();
  }



  onChangeName() {

    let code = this.addnewDirectory.value.name;
    this.checkTieuchi = this.addnewDirectory.value.name;
    const codeC = code.split(" ").pop();
    const split = codeC.split(".");
    const codeE = codeC.split(".").pop();
    const codeS = codeC.split(".")[0];
    this.checkCodeinput = codeC.split(".")[0];
    // console.log(code+" = code");
    // console.log(codeC+" =codeC");
    // console.log(split+" =split");
    // console.log(codeE+" = codeE");
    // console.log(codeS+" = codeS");
    console.log(this.checkCodeinput + " checkCodeinput");





    if (codeE % 1 == 0 && codeS % 1 == 0 && split.length == 2) {
      if (this.Code) {
        if (codeS.length == 1) {
          this.codeSS = "0" + codeS;
          this.codeSSS = "0" + codeS;
        } else {
          this.codeSS = codeS;
          this.codeSSS = codeS;

        }
        if (codeE.length == 1) {
          this.codeEE = "0" + codeE;
        } else {
          this.codeEE = codeE;
        }
      }
      else {
        this.codeSS = codeS;

        if (codeS.length == 1) {
          this.codeSSS = "0" + codeS;
        } else {
          this.codeSSS = codeS;
        }

        if (codeE.length == 1) {
          this.codeEE = "0" + codeE;
        } else {
          this.codeEE = codeE;
        }
      }
      this.autoCode = this.codeEXH + this.codeSS + "." + this.codeSSS + "." + this.codeEE;
    } else {
      this.autoCode = "";
    }

  }

  getListOrg() {
    let params = {
      method: "GET",
    };
    Swal.showLoading();
    this.service
      .getListOrganizationForCriteria(params)
      .then((data) => {
        Swal.close();
        let response = data;
        console.log(data.content);

        if (response.code === 0) {
          this.listOrg = data.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      // .catch((error) => {
      //   Swal.close();
      //   Swal.fire({
      //     icon: "error",
      //     title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
      //     confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      //   });
      // })

      ;
  }

  getListStandard() {
    let params = {
      method: "GET",
      orgId: this.addnewDirectory.value.organizationId.id,
      categoryId: this.addnewDirectory.value.categoryId.id,
    };

    // console.log(this.addnewDirectory.value.categoryId.id + "   CATE");

    Swal.showLoading();
    this.service
      .getListStaByOrgIdAndUsername(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          this.listStandard = data.content;
        } else {
          Swal.fire({
            icon: "error",
            title: response.errorMessages,
          });
        }
      })
      // .catch((error) => {
      //   Swal.close();
      //   Swal.fire({
      //     icon: "error",
      //     title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
      //     confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      //   });
      // })
      ;

  }

  initForm() {
    this.addnewDirectory = this.formBuilder.group(
      {
        name: ['', [Validators.required]],
        nameEn: [''],
        code: ['', [Validators.required]],
        standard: [null, [Validators.required]],
        organizationId: [null, [Validators.required]],
        userInfos: [null],
        description: [''],
        descriptionEn: [''],
        categoryId: [null, Validators.required],
      },
    );
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

  // getListPrograms(){
  //   let params = {
  //     method: "GET",
  //   };
  //   Swal.showLoading();
  //   this.service
  //     .getListPrograms(params)
  //     .then((data) => {
  //       Swal.close();
  //       let response = data;
  //       if (response.code === 0) {
  //         this.listPrograms = data.content;
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


  onChange() {
    console.log("id ", this.idSta)
    this.disableSta = false;
    // this.resetSta();
    // this.addnewDirectory.patchValue({
    //   standard: null
    // })

    this.getListStandard();
    // this.getStabyId();
  }
  onChange1() {
    if (this.idSta != null) {
      this.getStabyId();

    }
  }

  addDirectory() {
    this.addUserDirectorySubmitted = true;
    if (this.addnewDirectory.value.name !== '') {
      this.addnewDirectory.patchValue({
        name: this.addnewDirectory.value.name.trim()
      })
    }
    if (!this.checkTieuchi.includes("Tiêu chí")) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.CRITERIA.CHECK_ST'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      });
      return;
    }
    if (this.checkCode != this.checkCodeinput) {
      Swal.fire({
        icon: "error",
        title: this._translateService.instant('MESSAGE.CRITERIA.CHECKCODE'),
        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
      })
      return;
    }
    if (this.addnewDirectory.value.description !== '') {
      this.addnewDirectory.patchValue({
        description: this.addnewDirectory.value.description.trim()
      })
    }
    if (this.addnewDirectory.invalid) {
      return;
    }

    let content = this.addnewDirectory.value;
    content.standardId = this.addnewDirectory.value.standard;
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
            title: this._translateService.instant('MESSAGE.CRITERIA.ADD_SUCCESS'),
            confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
          }).then((result) => {
            // this.initForm();
            this.afterCreateDirectory.emit('completed');
          });
        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant('MESSAGE.CRITERIA.CRITERIA_EXISTED'),
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

  onChangePro() {
    this.disableCat = false;
    // this.disableSta = false;
    // this.resetSta();
    // this.addnewDirectory.patchValue({
    //   standard: null
    // })

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
        // .catch((error) => {
        //   Swal.close();
        //   Swal.fire({
        //     icon: "error",
        //     title: this._translateService.instant('MESSAGE.COMMON.CONNECT_FAIL'),
        //     confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
        //   });
        // })
        ;
    }


    // this.getListStandard();
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
            this.data = response.content;

            this.codeEXH = this.data.name;
            this.Code = this.data.enCode;

            if (this.addnewDirectory.value.name) {
              this.onChangeName();
            }

            // this.codeEXH = data.content;
            this.autoCode = this.codeEXH + this.codeSS + "." + this.codeSSS + "." + this.codeEE;
            // console.log(this.codeEXH);
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

  getListUser() {
    let params = {
      method: "GET",
    };
    // console.log("USer");

    this.directoryService
      .getListUser(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.listUser = data.content;
          // console.log(this.listUser);
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

  resetPro() {
    this.disableSta = true;
    this.resetSta();
    this.addnewDirectory.patchValue({
      standard: null
    })
    this.autoCode = "";
  }

  resetSta() {
    // this.autoCode = null;
  }

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

  getStabyId() {
    console.log("đầu vào");

    let params = {
      method: "GET",
      id: this.idSta,
    };
    // console.log("USer");

    this.service
      .findbyId(params)
      .then((data) => {
        let response = data;
        if (response.code === 0) {
          this.StaById = data.content;
          console.log(this.StaById + " oke4");

          console.log(data.content.name + " = Tên tiêu chuẩn");

          let name = data.content.name;
          this.checkCode = name.split(" ").pop();
          // let name2=name1.split(".");
          // this.checkCode= name2.split(".")[0];

          console.log(this.checkCode + " = check code");



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
