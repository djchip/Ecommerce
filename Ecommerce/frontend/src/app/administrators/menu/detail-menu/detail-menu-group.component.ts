import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Router } from "@angular/router";
import {MenuService} from '../menu.service';
import {elementAt} from "rxjs/operators";

@Component({
    selector: 'app-detail-menu',
    templateUrl: './detail-menu-group.component.html',
    styleUrls: ['./detail-menu-group.component.scss']
})
export class DetailMenuGroupComponent implements OnInit {

    public contentHeader: object;
    public listMenu = [{id:"1", name: "Level 1"}, {id:"2", name: "Level 2"}, {id:"3", name: "Level 3"}];
    public menuId;
    public menuParentId;
    public data: any;

    constructor(private router: Router, private service:MenuService) {}

    ngOnInit(): void {
        this.contentHeader = {
            headerTitle: 'Thông tin người dùng',
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
        this.getMenuDetail();
        this.getListMenu();
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
                    console.log("menu",this.listMenu)
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




    menuParrentName: string
    getMenuDetail(){
        if (this.menuId !== ''){
            let params = {
                method: "GET"
            };
            Swal.showLoading();
            this.service
                .detailMenu(params, this.menuId)
                .then((data) => {
                    Swal.close();
                    let response = data;
                    if (response.code === 0){
                        this.data = response.content;
                        this.listMenu.forEach(element =>{
                            if (element.id == this.data.menuParentId + ""){
                                this.menuParentId = element;
                                this.menuParrentName = this.menuParentId.menuName
                            }
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
                        icon : "error",
                        title:"can not reach Gateway.",
                        confirmButtonText: "OK",
                    });
                });
        }
    }




}