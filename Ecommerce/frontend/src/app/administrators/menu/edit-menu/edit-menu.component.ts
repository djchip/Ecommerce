import {ChangeDetectorRef, Component, EventEmitter, OnInit, Output} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import {MenuService} from '../menu.service';
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
@Component({
    selector : 'app-edit-menus-group',
    templateUrl : 'edit-menu.component.html',
    styleUrls : ['./edit-menu.component.scss']
})

export class EditMenuComponent implements OnInit {

    @Output() afterEditMenus = new EventEmitter<string>();

    public contentHeader : object;
    public listStatus = [];
    public listMenu = [];
    public editMenusFrom : FormGroup;
    public editMenusFromSubmitted = false;
    public data;
    public json = require('feather-icons/dist/icons.json');
    public listIcon;
    public menuParentId;
    public menuId;
    public status;
    public searchText;
    public chooseFeather = "";
    constructor(private formBuilder: FormBuilder, private service: MenuService, private modalService: NgbModal, private ref: ChangeDetectorRef) { }

    ngOnInit(): void {
        this.listIcon = this.json;
        this.getListMenu();
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
        this.menuId = window.localStorage.getItem("menuId");
        this.initFrom();
        this.getMenusDetail();
    }

    initFrom() {
        this.editMenusFrom = this.formBuilder.group(
            {
                id: ['', Validators.required],
                menuCode: ['', Validators.required],
                menuName: ['', Validators.required],
                url: ['', Validators.required],
                icon: ['', Validators.required],
                menuParentId : ['', Validators.required],
                translate : ['', Validators.required],

            },
        );
    }
    fillFrom(){
        this.editMenusFrom.patchValue(
            {
                id: this.data.id,
                menuCode: this.data.menuCode,
                menuName: this.data.menuName,
                url: this.data.url,
                status: this.status,
                icon: this.data.icon,
                translate: this.data.translate,
                menuParentId : this.menuParentId,
            },
        );
    }

    get EditMenuFrom(){
        return this.editMenusFrom.controls;
    }

    getListMenu(){
        let params = {
            method: "GET",
        };
        Swal.showLoading();
        this.service
            .getListMenuParrent(params)
            .then((data) => {
                Swal.close();
                let response = data;
                if (response.code === 0) {
                    this.listMenu = data.content;
                    console.log(this.listMenu)
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
        if (this.menuId !== ''){
            let params = {
                method: "GET"
            };
            Swal.showLoading();
            await this.service
                .detailMenu(params, this.menuId)
                .then((data) => {
                    Swal.close();
                    let response = data;
                    if (response.code === 0) {
                        this.data = response.content;
                        console.log("DATA",this.data)
                        this.listStatus.forEach(element =>{
                            if(element.id == this.data.status + ""){
                                this.status = element;
                            }
                        })

                        this.listMenu.forEach(element => {
                            if (element.id == this.data.menuParentId + ""){
                                this.menuParentId = element;
                            }
                        });

                        // this.menuParentId = this.listMenu.filter(el => el.id == this.data.menuParentId)
                        // console.log(this.menuParentId)

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

    editMenu(){
        this.editMenusFromSubmitted = true;

        if (this.editMenusFrom.value.menuName !==''){
            this.editMenusFrom.patchValue({
                menuName : this.editMenusFrom.value.menuName.trim()
            })
        }

        let content = this.editMenusFrom.value;
        content.menuParentId = this.editMenusFrom.value.menuParentId.id,
        content.status = this.editMenusFrom.value.status;

        let params = {
            method: "PUT",
            content: content
        };
        Swal.showLoading();
        this.service
            .editMenu(params)
            .then((data) => {
                Swal.close();
                let response = data;
                if (response.code === 0) {
                    Swal.fire({
                        icon: "success",
                        title: "Cập nhật menu thành công.",
                    }).then((result) => {
                        this.afterEditMenus.emit('completed');
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
                    title: "Không kết nối được tới hệ thống.",
                    confirmButtonText: "OK",
                });
            });

    }
    resetForm(){
        this.ngOnInit();
    }

    // modal Open Small
    modalOpenSM(modalSM) {
        this.modalService.open(modalSM, {
            centered: true,
            size: 'xl' // size: 'xs' | 'sm' | 'lg' | 'xl'
        });
    }

    copy(value) {
        this.editMenusFrom.patchValue({
            icon: value
        })
        this.chooseFeather = value;
        this.ref.detectChanges();
    }




}