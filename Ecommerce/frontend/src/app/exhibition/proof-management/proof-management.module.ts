import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { TranslateModule } from '@ngx-translate/core';
import { ListProofComponent } from './list-proof/list-proof.component';
import { AddProofComponent } from './add-proof/add-proof.component';
import { SearchProofComponent } from './search-proof/search-proof.component';
import { DetailProofComponent } from './detail-proof/detail-proof.component';
import { InputProofCodeComponent } from './input-proof-code/input-proof-code.component';
import { EditProofComponent } from './edit-proof/edit-proof.component';
import { TreeModule } from '@circlon/angular-tree-component';
import { CopyProofComponent } from './copy-proof/copy-proof.component';
import { ViewImportProofComponent } from './view-import-proof/view-import-proof.component';
import { FileUploadModule } from 'ng2-file-upload';
import { GetLinkComponent } from './get-link/get-link.component';
import { NgxMaskModule } from 'ngx-mask';
// routing
const routes: Routes = [
  {
    path: 'list',
    component: ListProofComponent,
  },    
  {
    path: 'search',
    component: GetLinkComponent,
  },
];

@NgModule({
  declarations: [
    ListProofComponent,
    AddProofComponent,
    SearchProofComponent,
    DetailProofComponent,
    InputProofCodeComponent,
    EditProofComponent,
    CopyProofComponent,
    ViewImportProofComponent,
    GetLinkComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    NgxDatatableModule,
    CorePipesModule,
    CoreDirectivesModule,
    NgbModule,
    TranslateModule,
    TreeModule,
    FileUploadModule,
    NgxMaskModule.forRoot(),
  ],
  exports:[
    DetailProofComponent,
    ViewImportProofComponent
  ]
})
export class ProofManagementModule { }
