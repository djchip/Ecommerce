import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { ListRoleMenuPrivilegesComponent } from './list-role-menu-privileges/list-role-menu-privileges.component';
import { AddRoleMenuPrivilegesComponent } from './add-role-menu-privileges/add-role-menu-privileges.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { DetailRoleMenuPrivilegesComponent } from './detail-role-menu-privileges/detail-role-menu-privileges.component';
import { EditRoleMenuPrivilegesComponent } from './edit-role-menu-privileges/edit-role-menu-privileges.component';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
const routes: Routes = [
    {
        path: 'list-role-menu',
        component: ListRoleMenuPrivilegesComponent,
    },
    {
        path: 'add-role-menu',
        component: AddRoleMenuPrivilegesComponent,
    },
    {
        path: 'detail-role-menu',
        component: DetailRoleMenuPrivilegesComponent,
    },
    {
        path: 'edit-role-menu',
        component: EditRoleMenuPrivilegesComponent,
    }
];

@NgModule({
    declarations: [
        ListRoleMenuPrivilegesComponent,
        AddRoleMenuPrivilegesComponent,
        DetailRoleMenuPrivilegesComponent,
        EditRoleMenuPrivilegesComponent
    ],
    imports: [
        NgSelectModule,
        RouterModule.forChild(routes),
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        NgxDatatableModule,
        CorePipesModule,
        CoreDirectivesModule,
        NgbModule,
        ContentHeaderModule
    ]
})
export class RoleMenuPrivilegesModule { }
