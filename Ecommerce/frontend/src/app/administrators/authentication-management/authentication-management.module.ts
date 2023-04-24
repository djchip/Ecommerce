import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { EditAuthenticationComponent } from './edit-authentication/edit-authentication.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { TreeModule } from '@circlon/angular-tree-component';

const routes: Routes = [
  {
    path: 'edit-authentication',
    component: EditAuthenticationComponent,
  }
];

@NgModule({
  declarations: [
    EditAuthenticationComponent
  ],
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    NgSelectModule,
    ContentHeaderModule,
    TranslateModule,
    TreeModule
  ]
})
export class AuthenticationManagementModule { }
