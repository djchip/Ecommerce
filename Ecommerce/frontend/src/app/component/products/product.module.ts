import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ListProductComponent } from './list-product/list-product.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CoreDirectivesModule } from '@core/directives/directives';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgSelectModule } from '@ng-select/ng-select';
import { TranslateModule } from '@ngx-translate/core';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { ContentHeaderModule } from 'app/layout/components/content-header/content-header.module';
import { AddProductComponent } from './add-product/add-product.component';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatListModule } from '@angular/material/list';
import { FileUploadModule } from 'ng2-file-upload';
import { EditProductComponent } from './edit-product/edit-product.component';
import { DetailProductComponent } from './detail-product/detail-product.component';
// import { NoopAnimationsModule } from '@angular/platform-browser/animations';

const materialModules = [
  MatCardModule,
  MatToolbarModule,
  MatButtonModule,
  MatInputModule,
  MatFormFieldModule,
  MatProgressBarModule,
  MatListModule
];

const routes: Routes = [
  {
    path: 'list',
    component: ListProductComponent,
    data: { animation: 'feather' }
  }
];

@NgModule({
  declarations: [
    ListProductComponent,
    AddProductComponent,
    EditProductComponent,
    DetailProductComponent
  ],
  imports: [
    CommonModule,
    NgbModule,
    CoreDirectivesModule,
    NgxDatatableModule,
    CorePipesModule,
    ReactiveFormsModule,
    FormsModule,
    NgSelectModule,
    ContentHeaderModule,
    RouterModule.forChild(routes),
    TranslateModule,
    FileUploadModule,
    // NoopAnimationsModule,
    ...materialModules
  ]
})
export class ProductModule { }
