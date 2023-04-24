import { DetailUserComponent } from './user-management/detail-user/detail-user.component';
import { CoreSidebarModule } from '@core/components';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreCommonModule } from '@core/common.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FileUploadModule } from 'ng2-file-upload';
import { ErrorHandler, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { UserManagementModule } from './user-management/user-management.module';
import { MenuModule } from './menu/menu.module';
import { RolesGroupManagementModule } from './roles-group-management/roles-group-management.module';
import { NgSelectModule } from '@ng-select/ng-select';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { SoftwareLogModule } from './software-log/software-log.module';
import {AuthenticationManagementModule} from './authentication-management/authentication-management.module';
import {DetailEmailComponent} from "./email-config/detail-email/detail-email.component";
import {EditEmailComponent} from "./email-config/edit-email/edit-email.component";
import {ContentHeaderModule} from "../layout/components/content-header/content-header.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { TreeviewModule } from 'ngx-treeview';
import {QuillModule} from "ngx-quill";
import {CoreDirectivesModule} from "../../@core/directives/directives";
import {DocumentEditorContainerAllModule} from "@syncfusion/ej2-angular-documenteditor";
import { AuthGuard } from 'app/auth/helpers/auth.guards';
import { EmailComposeComponent } from './email-config/email-compose/email-compose.component';
import { PerfectScrollbarModule } from 'ngx-perfect-scrollbar';
import { DetailRolesGroupComponent } from './roles-group-management/detail-roles-group/detail-roles-group.component';
import { ListConfigComponent } from './config-others/list-config/list-config.component'
import { ConfigModule } from './config-others/config.module';
import {DetailSoftwareLogComponent} from "./software-log/detail-software-log/detail-software-log.component";
import { DetailDocumentComponent } from './config-others/document-type-config/detail-document/detail-document.component';
import { DetailExhCodeComponent } from './config-others/exh-code-config/detail-exh-code/detail-exh-code.component';
import { TranslateModule } from '@ngx-translate/core';


// routing
const routes: Routes = [
  {
    path: 'user',
    loadChildren: () => import('./user-management/user-management.module').then(m => m.UserManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'log',
    loadChildren: () => import('./log-management/log.module').then(m => m.LogModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'menu',
    loadChildren: () => import('./menu/menu.module').then(m => m.MenuModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'roles-group',
    loadChildren: () => import('./roles-group-management/roles-group-management.module').then(m => m.RolesGroupManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'privileges',
    loadChildren: () => import('./privileges-management/privileges-management.module').then(m => m.PrivilegesManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'role-menu',
    loadChildren: () => import('./roles-menu/role-menu-privileges.module').then(m => m.RoleMenuPrivilegesModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'authentication',
    loadChildren: () => import('./authentication-management/authentication-management.module').then(m => m.AuthenticationManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'role-privileges',
    loadChildren: () => import('./role-privileges/role-privileges.module').then(m => m.RolePrivilegesModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'undo',
    loadChildren: () => import('./log-undo/log-undo.module').then(m => m.LogUndoModule),
    canActivate: [AuthGuard]
  },
  {
    path:'software-log',
    loadChildren: () => import('./software-log/software-log.module').then(m => m.SoftwareLogModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'email-config',
    component: DetailEmailComponent,
    canActivate: [AuthGuard]
  },
  {
    path:'config',
    loadChildren: () => import('./config-others/config.module').then(m => m.ConfigModule),
    canActivate: [AuthGuard]
  },

  {
    path:'proof-tree',
    loadChildren: () => import('./tree-proof/tree-proof.module').then(m => m.TreeProofLogModule),
    canActivate: [AuthGuard]
  },

  


];

@NgModule({
  declarations: [
    DetailEmailComponent,
    EditEmailComponent,
    EmailComposeComponent,
  ],
  imports: [
    NgSelectModule,
    NgxDatatableModule,
    CommonModule,
    UserManagementModule,
    RouterModule.forChild(routes),
    MenuModule,
    RolesGroupManagementModule,
    AuthenticationManagementModule,
    FileUploadModule,
    ContentHeaderModule,
    NgbModule,
    CoreCommonModule,
    SoftwareLogModule,
    CoreDirectivesModule,
    TreeviewModule.forRoot(),
    // QuillModule,
    FormsModule,
    ReactiveFormsModule,
    DocumentEditorContainerAllModule,
    PerfectScrollbarModule,
    QuillModule.forRoot(),
    CorePipesModule,
    CoreSidebarModule,
    ConfigModule,
    TranslateModule
  ],
  exports: [
    DetailUserComponent,
    DetailRolesGroupComponent,
    DetailSoftwareLogComponent,
    DetailDocumentComponent,
    DetailExhCodeComponent
  ]
})
export class AdministratorsModule { }
