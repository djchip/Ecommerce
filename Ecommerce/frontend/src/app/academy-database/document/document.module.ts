import { DetailDocumentComponent } from './detail-document/detail-document.component';
import { EditDocumentComponent } from './edit-document/edit-document.component';
import { TranslateModule } from '@ngx-translate/core';
import { AddDocumentComponent } from './add-document/add-document.component';
import { ListDocumentComponent } from './list-document/list-document.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CopyDocumentComponent } from './copy-document/copy-document.component';
import { TreeModule } from '@circlon/angular-tree-component';
import { InputCodeComponent } from './input-code/input-code.component';



const routes: Routes = [
  {
    path: 'list',
    component: ListDocumentComponent,
    data: { animation: 'feather' }
  }
]

@NgModule({
  declarations: [
    ListDocumentComponent,
    AddDocumentComponent,
    EditDocumentComponent,
    DetailDocumentComponent,
    CopyDocumentComponent,
    InputCodeComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContentHeaderModule,
    NgSelectModule,
    FormsModule,
    ReactiveFormsModule,
    CorePipesModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    TranslateModule,
    TreeModule,
  ],
  exports:[
    DetailDocumentComponent,
  ]
})
export class DocumentModule { }
