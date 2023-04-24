import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import Swal from "sweetalert2";
import {EmailConfigService} from "../email-config.service";
import {CoreTranslationService} from "../../../../@core/services/translation.service";
import {TranslateService} from "@ngx-translate/core";

@Component({
    selector: 'app-edit-email',
    templateUrl: './edit-email.component.html',
    styleUrls: ['./edit-email.component.scss']
})
export class EditEmailComponent implements OnInit {

    public editEmailConfig: FormGroup;
    public data;
    public email;
    public host;
    public port;
    public username;
    public password;


    @Output() afterEdit = new EventEmitter<string>();

    constructor(
        private service: EmailConfigService,
        private formBuilder: FormBuilder,
        private _coreTranslationService: CoreTranslationService,
        public _translateService: TranslateService
    ) {
    }

    ngOnInit(): void {
        this.initForm();
        this.getUserDetail();
    }

    initForm() {
        this.editEmailConfig = this.formBuilder.group(
            {
                email: ["", [Validators.required, Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)]],
                host: ["", Validators.required],
                port: ["", Validators.required],
                username: ["", Validators.required],
                password: ['', [Validators.pattern("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")]],
                id: [""],
            },
        );
    }

    getUserDetail() {
        let params = {
            method: "GET"
        };
        Swal.showLoading();
        this.service
            .detailEmailConfig(params)
            .then((data) => {
                Swal.close();
                let response = data;
                if (response.code === 0) {
                    this.data = response.content.items[0];
                    // console.log("==========================this.data", this.data);
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

    fillForm(){
        this.editEmailConfig.patchValue(
            {
                email: this.data.email,
                host: this.data.host,
                port: this.data.port,
                username: this.data.username,
                password: this.data.password,
                id: this.data.id,
            },
        );
    }

    resetForm() {
        this.fillForm();
    }

    editUser() {
        console.log("===============this",this.editEmailConfig.value);
        let content = this.editEmailConfig.value;

        let params = {
            method: "PUT",
            content: content
        };

        Swal.showLoading();
        this.service
            .updateEmailConfig(params)
            .then((data) => {
                Swal.close();
                let response = data;
                if (response.code === 0) {
                    Swal.fire({
                        icon: "success",
                        title: this._translateService.instant('MESSAGE.EMAIL_CONFIG.UPDATE_SUCCESS'),
                        confirmButtonText: this._translateService.instant('ACTION.ACCEPT'),
                    }).then((result) => {
                        this.afterEdit.emit('completed');
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

}
