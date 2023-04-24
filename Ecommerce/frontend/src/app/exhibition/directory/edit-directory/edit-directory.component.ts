import {
  ChangeDetectorRef,
  Component,
  OnInit,
  Output,
  EventEmitter,
  Input,
} from "@angular/core";
import { FormGroup, FormBuilder, Validators } from "@angular/forms";
import { DirectoryService } from "../directory.service";
import Swal from "sweetalert2";
import { TranslateService } from "@ngx-translate/core";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { ChangeLanguageService } from "app/services/change-language.service";
import { ValidateName } from "../space.validator";
import { TokenStorage } from "app/services/token-storage.service";

@Component({
  selector: "app-edit-directory",
  templateUrl: "./edit-directory.html",
  styleUrls: ["./edit-directory.scss"],
})
export class EditDirectoryComponent implements OnInit {
  @Output() afterEditDirectory = new EventEmitter<string>();

  public contentHeader: object;
  public addDirectory: FormGroup;
  public addDirectorySubmitted = false;
  public data;
  @Input() id;
  public listOrg = [];
  public dirLoading = false;
  public mergedPwdShow = false;
  public roleLoading = false;
  public chooseFeather = "";
  public listPrograms = [];
  public codeSta = "00";
  public codeEXH;
  public Code = false;
  selectedRole1 = null;
  public currentLang = this._translateService.currentLang;
  public roleAdmin = window.localStorage.getItem("ADM");
  public listUser;
  public currentUserName;
  public listCat=[];

  constructor(
    private ref: ChangeDetectorRef,
    private modalService: NgbModal,
    private tokenStorage: TokenStorage,
    private formBuilder: FormBuilder,
    private service: DirectoryService,
    public _translateService: TranslateService,
    private _changeLanguageService: ChangeLanguageService
  ) {
    this._changeLanguageService.componentMethodCalled$.subscribe(() => {
      this.currentLang = this._translateService.currentLang;
    });
  }

  ngOnInit(): void {
    this.currentUserName = this.tokenStorage.getUsername();
    this.getLisCategories();
    this.getListOrg();
    this.initForm();
    this.getListUser();
    this.getDirectoryDetail();
  }

  initForm() {
    this.addDirectory = this.formBuilder.group({
      id: ["", [Validators.required]],
      code: ["", [Validators.required]],
      name: ["", [Validators.required, ValidateName]],
      nameEn: [""],
      description: [''],
      descriptionEn: [''],
      // userInfos: [null],
      organizationId: ["", [Validators.required]],
      categoryId:["",Validators.required],
      
    });
  }

  onChange() {
    if (this.addDirectory.value.organizationId) {
      let params = {
        method: "GET",
      };
      this.service
        .getCodeEXH(params, this.addDirectory.value.organizationId)
        .then((data) => {
          let response = data;
          if (response.code === 0) {
            // this.codeEXH = data.content;
            this.data = response.content;
            this.codeEXH = this.data.name;
            this.Code = this.data.enCode;

            this.addDirectory.patchValue({
              code: this.codeEXH + this.codeSta,
            });
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
            title: this._translateService.instant(
              "MESSAGE.COMMON.CONNECT_FAIL"
            ),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          });
        });
    }
  }

  resetPro() {
    this.addDirectory.patchValue({
      code: this.codeEXH + this.codeSta,
    });
  }

  onChangeName() {
    let code = this.addDirectory.value.name;
    const codeS = code.split(" ").pop();
    if(this.Code){
      codeS.length < 2 ? this.codeSta = "0" + codeS : this.codeSta = codeS;
    }
    else {
       this.codeSta = codeS;
    }
        this.addDirectory.patchValue({
      code: this.codeEXH + this.codeSta,
    });
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
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      })
      .finally(() => {
        this.getDirectoryDetail();
      });
  }

  fillForm() {
    this.addDirectory.patchValue({
      id: this.data.entity.id,
      name: this.data.entity.name,
      nameEn: this.data.entity.nameEn,
      description: this.data.entity.description,
      descriptionEn: this.data.entity.descriptionEn,
      code: this.data.entity.code,
      // userInfos: this.data.userInfos,
      organizationId: this.data.entity.organizaId,
      categoryId: this.data.entity.categoryId,
    });
  }

  get AddDirectory() {
    return this.addDirectory.controls;
  }


  async getDirectoryDetail() {
    if (this.id !== "") {
      let params = {
        method: "GET",
      };
      Swal.showLoading();
      await this.service
        .getDirectory(params, this.id)
        .then((data) => {
          Swal.close();
          let response = data;
          if (response.code === 0) {
            this.data = response.content;
            this.fillForm();
            let code = this.data.entity.name;
            const codeS = code.split(" ").pop();
            if(this.Code){
              codeS.length < 2 ? this.codeSta = "0" + codeS : this.codeSta = codeS;
            }
            else {
               this.codeSta = codeS;
            }
              // this.codeSta = codeS;

            // if(codeS%1 == 0){
            //   this.codeSta = codeS;
            // }
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
            title: this._translateService.instant(
              "MESSAGE.COMMON.CONNECT_FAIL"
            ),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          });
        })
        .finally(() => {
          this.onChange();
        });
    }
  }

  editDirectory() {
    this.addDirectorySubmitted = true;
    if (this.addDirectory.value.name) {
      this.addDirectory.patchValue({
        name: this.addDirectory.value.name.trim(),
      });
    }
    if (this.addDirectory.value.description) {
      this.addDirectory.patchValue({
        description: this.addDirectory.value.description.trim(),
      });
    }
    if (this.addDirectory.value.code) {
      this.addDirectory.patchValue({
        code: this.addDirectory.value.code.trim(),
      });
    }
    if (this.addDirectory.invalid) {
      return;
    }

    let content = this.addDirectory.value;
    content.status = this.addDirectory.value.status;

    let params = {
      method: "PUT",
      content: content,
    };
    Swal.showLoading();
    this.service
      .editDirectory(params)
      .then((data) => {
        Swal.close();
        let response = data;
        if (response.code === 0) {
          Swal.fire({
            icon: "success",
            title: this._translateService.instant(
              "MESSAGE.DIRECTORY.UPDATE_SUCCESS"
            ),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          }).then((result) => {
            this.afterEditDirectory.emit("completed");
          });
        } else if (response.code === 3) {
          Swal.fire({
            icon: "error",
            title: this._translateService.instant(
              "MESSAGE.DIRECTORY.DIRECTORY_EXISTED"
            ),
            confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
          }).then((result) => {});
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
          title: this._translateService.instant("MESSAGE.COMMON.CONNECT_FAIL"),
          confirmButtonText: this._translateService.instant("ACTION.ACCEPT"),
        });
      });
  }

  resetForm() {
    this.getDirectoryDetail();
  }

  // modal Open Small
  modalOpenSM(modalSM) {
    this.modalService.open(modalSM, {
      centered: true,
      size: "xl", // size: 'xs' | 'sm' | 'lg' | 'xl'
    });
  }

  copy(value) {
    this.addDirectory.patchValue({
      icon: value,
    });
    this.chooseFeather = value;
    this.ref.detectChanges();
  }

  getListUser(){
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
}
