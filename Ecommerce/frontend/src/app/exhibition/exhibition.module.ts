import { ViewImportProofComponent } from './proof-management/view-import-proof/view-import-proof.component';
import { ViewImportCriteriaComponent } from './criteria/view-import-criteria/view-import-criteria.component';
import { ViewImportDirectoryComponent } from './directory/view-import-directory/view-import-directory.component';
import { UploadFormWizardModule } from './upload-form-wizard/upload-form-wizard.module';
import { AutoImportModule } from './auto-import/auto-import.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { OrganizationModule } from './organization/organization.module';
import { RouterModule, Routes } from '@angular/router';
import { AutoUploadModule } from './auto-upload/auto-upload.module';
import { TreeModule } from '@circlon/angular-tree-component';
import { AuthGuard } from 'app/auth/helpers/auth.guards';
import { CategoriesModule } from './categories/categories.module';
import { CriteriaModule } from './criteria/criteria.module';
import { DirectoryModule } from './directory/directory.module';
import { ProgramsManagementModule } from './programs-management/programs-management.module';
import { ProofManagementModule } from './proof-management/proof-management.module';
import { DetailDirectoryComponent } from './directory/detail-directory/detail-directory.component';
import { DetaiCriteriaComponent } from './criteria/detail-criteria/detail-criteria.component';
import { DetailProgramsComponent } from './programs-management/detail-programs/detail-programs.component'
;import { DetailProofComponent } from './proof-management/detail-proof/detail-proof.component';

import { DetailCategoriesComponent } from './categories/detail-categories/detail-categories.component';
import { DetailComponent } from './organization/detail/detail.component';
import { PrivilegesProStaCriComponent } from './privileges/privileges-pro-sta-cri/privileges-pro-sta-cri.component';
import { PrivilegesProStaCriModule } from './privileges/privileges-pro-sta-cri.module';

// routing
const routes: Routes = [
  {
    path: 'org',
    loadChildren: () => import('./organization/organization.module').then(m => m.OrganizationModule)
  },
  {
    path: 'auto-upload',
    loadChildren: () => import('./auto-upload/auto-upload.module').then(m => m.AutoUploadModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'category',
    loadChildren: () => import('./categories/categories.module').then(m => m.CategoriesModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'criteria',
    loadChildren: () => import('./criteria/criteria.module').then(m => m.CriteriaModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'standard',
    loadChildren: () => import('./directory/directory.module').then(m => m.DirectoryModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'program',
    loadChildren: () => import('./programs-management/programs-management.module').then(m => m.ProgramsManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'proof',
    loadChildren: () => import('./proof-management/proof-management.module').then(m => m.ProofManagementModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'auto-import',
    loadChildren: () => import('./auto-import/auto-import.module').then(m => m.AutoImportModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'upload-form-wizard',
    loadChildren: () => import('./upload-form-wizard/upload-form-wizard.module').then(m => m.UploadFormWizardModule),
    canActivate: [AuthGuard]
  },
  {
    path: 'privileges-pro-sta-cri',
    loadChildren: () => import('./privileges/privileges-pro-sta-cri.module').then(m => m.PrivilegesProStaCriModule),
    canActivate: [AuthGuard]
  }
];

@NgModule({
  declarations: [
  ],
  imports: [
    CommonModule,
    CorePipesModule,
    OrganizationModule,
    RouterModule.forChild(routes),
    AutoUploadModule,
    TreeModule,
    CategoriesModule,
    CriteriaModule,
    DirectoryModule,
    ProgramsManagementModule,
    ProofManagementModule,
    AutoImportModule,
    UploadFormWizardModule,
    PrivilegesProStaCriModule
  ],
  exports: [
    DetailDirectoryComponent,
    DetaiCriteriaComponent,
    DetailProgramsComponent,
    DetailCategoriesComponent,
    DetailProofComponent,
    DetailComponent,
    ViewImportDirectoryComponent,
    ViewImportCriteriaComponent,
    ViewImportProofComponent
  ]
})
export class ExhibitionModule { }
